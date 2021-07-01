/*********************************************************************************************
 *
 * 'Main.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.core.lang.generator;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import gama.core.lang.GamlStandaloneSetupGenerated;

public class Main {

	public static void main(final String[] args) {
		if (args.length == 0) {
			System.err.println("Aborting: no path to EMF resource provided!");
			return;
		}
		final Injector injector =
				new GamlStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
		final Main main = injector.getInstance(Main.class);
		main.runGenerator(args[0]);
	}

	@Inject private Provider<ResourceSet> resourceSetProvider;

	@Inject private IResourceValidator validator;

	@Inject private IGenerator generator;

	@Inject private JavaIoFileSystemAccess fileAccess;

	protected void runGenerator(final String string) {
		// load the resource
		final ResourceSet set = resourceSetProvider.get();
		final Resource resource = set.getResource(URI.createURI(string, false), true);

		// validate the resource
		final List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if (!list.isEmpty()) {
			for (final Issue issue : list) {
				System.out.println(issue.toString());
			}
			return;
		}

		// configure and start the generator
		fileAccess.setOutputPath("src-gen/");
		generator.doGenerate(resource, fileAccess);

		System.out.println("Code generation finished.");
	}
}
