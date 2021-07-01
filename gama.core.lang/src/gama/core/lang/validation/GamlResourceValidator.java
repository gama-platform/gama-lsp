/*********************************************************************************************
 *
 * 'GamlResourceValidator.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.core.lang.validation;

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.service.OperationCanceledError;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IDiagnosticConverter;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;

public class GamlResourceValidator implements IResourceValidator {

	@Inject IDiagnosticConverter converter;
	// private static ErrorToDiagnoticTranslator errorTranslator = new ErrorToDiagnoticTranslator();

	@Override
	public List<Issue> validate(Resource resource, CheckMode mode, CancelIndicator indicator)
			throws OperationCanceledError {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<Issue> validate(final Resource resource, final CheckMode mode, final CancelIndicator indicator) {
//		// DEBUG.OUT("GamlResourceValidato begginning validation job of " + resource.getURI().lastSegment());
//		try (final Collector.AsList<Issue> result = Collector.getList();) {
//			final IAcceptor<Issue> acceptor = t -> {
//				if (t.getMessage() != null && !t.getMessage().isEmpty()) {
//					result.add(t);
//				}
//			};
//			// We resolve the cross references
//			EcoreUtil2.resolveLazyCrossReferences(resource, indicator);
//			// DEBUG.OUT("Cross references resolved for " + resource.getURI().lastSegment());
//			// And collect the syntax / linking issues
//			for (int i = 0; i < resource.getErrors().size(); i++) {
//				converter.convertResourceDiagnostic(resource.getErrors().get(i), Severity.ERROR, acceptor);
//			}
//
//			// We then ask the resource to validate itself
//			final GamlResource r = (GamlResource) resource;
//			r.validate();
//			// DEBUG.OUT("Resource has been validated: " + resource.getURI().lastSegment());
//			// And collect the semantic errors from its error collector
//			for (final Diagnostic d : errorTranslator.translate(r.getValidationContext(), r, mode).getChildren()) {
//				converter.convertValidatorDiagnostic(d, acceptor);
//			}
//			GamlResourceServices.discardValidationContext(r);
//			// DEBUG.OUT("Validation context has been discarded: " + resource.getURI().lastSegment());
//			return result.items();
//		}
//	}

}
