/*******************************************************************************************************
 *
 * msi.gama.util.graph.loader.AvailableGraphParsers.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Lists all the graph importer available.
 * If you add a parser, add it there.
 *
 * @author Patrick Taillandier
 *
 */
public class GraphImporters {

	private static final Map<String, Class<? >> name2parser = null;

	/**
	 * contains the name of parsers for automatic detection. Should only contain
	 * each parser one time. Also, should be provided in the relevant order (more frequent formats first).
	 */
		private static final List<String> parsersForAutomaticDetection = new LinkedList<String>() {

			{
				add("graphml");
				add("gexf");
				add("dimacs");

				add("graph6");
				add("gml");
				add("tsplib");
				
				add("json");

				add("csv");
				

			}
		};

	/**
	 * Returns the list of all the names of loader declared.
	 * Typically required for interaction with users (like propose
	 * the list of all the possible loader, or search something passed
	 * by the user).
	 * @return
	 */
		public static Set<String> getAvailableLoaders() {
		return name2parser.keySet();
	}

	/**
	 * Returns a list of loaders declared, by ensuring that each loader is provided once
	 * only. So all the redondancies are removed.
	 * Typically used for automatic type detection.
	 * @return
	 */
	public static List<String> getLoadersForAutoDetection() {
		return parsersForAutomaticDetection;
	}

	private static Map<String, Object> name2singleton = new HashMap<String, Object>();

	public static Object getGraphImporter(final String fileType) {
		return null;
	}

}
