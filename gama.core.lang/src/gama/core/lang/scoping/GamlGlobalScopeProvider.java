package gama.core.lang.scoping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

import org.checkerframework.common.reflection.qual.GetClass;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

import com.google.inject.Singleton;

import gama.core.lang.common.interfaces.IGamlDescription;
import gama.core.lang.gaml.GamlDefinition;
import gama.core.lang.gaml.GamlPackage;

@Singleton
public class GamlGlobalScopeProvider extends ImportUriGlobalScopeProvider {
	public static EClass eType, eVar, eSkill, eAction, eUnit, eEquation;
	public static HashMap<EClass, Resource> resources = new HashMap<EClass, Resource>();
	public static HashMap<EClass, HashMap<QualifiedName, IEObjectDescription>> descriptions = new HashMap<EClass, HashMap<QualifiedName, IEObjectDescription>>();
	public static HashSet<QualifiedName> allNames = new HashSet<QualifiedName> ();
	public static XtextResourceSet rs = new XtextResourceSet();
	public static boolean initialized = false;

	static <V, K> HashMap<K, V> createResourceMap() {
		final HashMap<K, V> map = new HashMap<K, V>();
		return map;
	}

	static Resource createResource(final String uri) {
		Resource r = rs.getResource(URI.createURI(uri, false), false);
		if (r == null) {
			r = rs.createResource(URI.createURI(uri, false));
		}
		return r;
	}
	
	/*
	 * This method should be the only entry point, merging createDescription
	 */
	public static void initResources() {
		if (initialized) {
			return;
		}
		eType = GamlPackage.eINSTANCE.getTypeDefinition();
		eVar = GamlPackage.eINSTANCE.getVarDefinition();
		eSkill = GamlPackage.eINSTANCE.getSkillFakeDefinition();
		eAction = GamlPackage.eINSTANCE.getActionDefinition();
		eUnit = GamlPackage.eINSTANCE.getUnitFakeDefinition();
		eEquation = GamlPackage.eINSTANCE.getEquationDefinition();
		resources.put(eType, getResourceFromFile("gama/core/lang/scoping/TypeDefinition.xmi"));
		resources.put(eVar, getResourceFromFile("gama/core/lang/scoping/VarDefinition.xmi"));
		resources.put(eUnit, getResourceFromFile("gama/core/lang/scoping/UnitFakeDefinition.xmi"));
		resources.put(eSkill, getResourceFromFile("gama/core/lang/scoping/SkillFakeDefinition.xmi"));
		resources.put(eAction, getResourceFromFile("gama/core/lang/scoping/ActionDefinition.xmi"));
		resources.put(eEquation, getResourceFromFile("gama/core/lang/scoping/EquationDefinition.xmi"));
		descriptions.put(eVar, createResourceMap());
		descriptions.put(eType, createResourceMap());
		descriptions.put(eSkill, createResourceMap());
		descriptions.put(eUnit, createResourceMap());
		descriptions.put(eAction, createResourceMap());
		descriptions.put(eEquation, createResourceMap());
		allNames = new HashSet<>();
		initialized = true;
	}
	
	@Override
	public IScope getScope(Resource useless, EReference reference) {
		initResources();
		EClass cl = reference.getEContainingClass();
		return super.getScope(resources.get(cl), reference);
	}
	
	
	public static Resource getResourceFromFile(String path) {
		InputStream file = getFileFromResourceAsStream(path);
		XMIResource r = new XMIResourceImpl();
		try {
			r.load(file, Collections.EMPTY_MAP);
			System.out.println("[" + path + "] is loaded: " + r.isLoaded());
			return r;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * refer to:
	 * https://mkyong.com/java/java-read-a-file-from-resources-folder/
	 */
	public static InputStream getFileFromResourceAsStream(String fileName) {

        /*
         * The class loader that loaded the class
         * Refer to:
         * https://stackoverflow.com/questions/8275499/how-to-call-getclass-from-a-static-method-in-java
         */
        ClassLoader classLoader = GamlGlobalScopeProvider.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
