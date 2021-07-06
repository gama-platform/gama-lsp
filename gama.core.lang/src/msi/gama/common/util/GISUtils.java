/*******************************************************************************************************
 *
 * msi.gama.common.util.GISUtils.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GISUtils {

	// ugly method to manage Google CRS.... hoping that it is better managed by the next versions of Geotools
	public static Object manageGoogleCRS(final URL url) {
		return null;
	}
}
