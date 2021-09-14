package gama.core.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.xtext.ide.server.ServerLauncher;

import gama.core.lang.scoping.GamlGlobalScopeProvider;


public class RunLSP {

	public static void main(String[] args) {
		ServerLauncher.main(args);
	}
	
	public static void print(String a) {
		System.out.println(a);
	}
	
	public static void debugResource(String path) {

		ResourceSet rs = GamlGlobalScopeProvider.rs;
		Map<Object, Object> opt = rs.getLoadOptions();
		XMIResource r = new XMIResourceImpl();
		// Resource r = new ResourceImpl(URI.createFileURI(path));
		InputStream f = GamlGlobalScopeProvider.getFileFromResourceAsStream(path);
		try {
			r.load(f, Collections.EMPTY_MAP);
			print("" + r.isLoaded());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		TreeIterator<EObject> iter = r.getAllContents();
//		iter.forEachRemaining(c -> {
//			print(c.getClass().getName());
//		});
		// Resource r = rs.getResource(URI.createFileURI(path), false);
	}
}
