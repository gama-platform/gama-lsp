package gama.core.lang.scoping;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

import com.google.inject.Singleton;

import gama.core.lang.gaml.GamlPackage;

@Singleton
public class GlobalScopeProvider extends ImportUriGlobalScopeProvider {
	private static EClass eType, eVar, eSkill, eAction, eUnit, eEquation;
	private static HashMap<EClass, Resource> resources;
	private static HashMap<EClass, HashMap<QualifiedName, IEObjectDescription>> descriptions;
	private static HashSet<QualifiedName> allNames;
	static XtextResourceSet rs = new XtextResourceSet();

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

	static void initResources() {
		eType = GamlPackage.eINSTANCE.getTypeDefinition();
		eVar = GamlPackage.eINSTANCE.getVarDefinition();
		eSkill = GamlPackage.eINSTANCE.getSkillFakeDefinition();
		eAction = GamlPackage.eINSTANCE.getActionDefinition();
		eUnit = GamlPackage.eINSTANCE.getUnitFakeDefinition();
		eEquation = GamlPackage.eINSTANCE.getEquationDefinition();
		resources = createResourceMap();
		resources.put(eType, createResource("types.xmi"));
		resources.put(eVar, createResource("vars.xmi"));
		resources.put(eSkill, createResource("skills.xmi"));
		resources.put(eUnit, createResource("units.xmi"));
		resources.put(eAction, createResource("actions.xmi"));
		resources.put(eEquation, createResource("equations.xmi"));
		descriptions = createResourceMap();
		descriptions.put(eVar, createResourceMap());
		descriptions.put(eType, createResourceMap());
		descriptions.put(eSkill, createResourceMap());
		descriptions.put(eUnit, createResourceMap());
		descriptions.put(eAction, createResourceMap());
		descriptions.put(eEquation, createResourceMap());
		allNames = new HashSet<>();
	}
}
