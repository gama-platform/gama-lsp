/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.AvailableGraphWriters.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.graph.writer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Lists available graphs writers, independently of the underlying library.
 *
 * @author Patrick Taillandier
 *
 */
public class GraphExporters {

	private static final Map<String, Class<? extends Object>> name2writer = new HashMap<String, Class<? extends Object>>() {

	};

	public static Set<String> getAvailableWriters() {
		return name2writer.keySet();
	}

	private static Map<String, Object> name2singleton = new HashMap<>();

	public static Object getGraphWriter(final String name) {
		Object res = name2singleton.get(name);

		if (res == null) {
			return null;
		}
		return res;
	}
}
