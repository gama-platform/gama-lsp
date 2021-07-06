/*******************************************************************************************************
 *
 * msi.gama.common.geometry.GamaGeometryFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import java.util.List;



import msi.gama.metamodel.shape.GamaPoint;

public class GamaGeometryFactory {

	public static final GamaCoordinateSequenceFactory COORDINATES_FACTORY = new GamaCoordinateSequenceFactory();
	public static final Object JTS_COORDINATES_FACTORY = null;

	public GamaGeometryFactory() {
	}

	public static boolean isRing(final Object[] pts) {
		if (pts.length < 4) { return false; }
		if (!pts[0].equals(pts[pts.length - 1])) { return false; }
		return true;
	}

	public static boolean isRing(final List<GamaPoint> pts) {
		final int size = pts.size();
		if (size < 4) { return false; }
		if (!pts.get(0).equals(pts.get(size - 1))) { return false; }
		return true;
	}

	// public static boolean isRing(final double[] array) {
	// final int size = array.length;
	// if (size < 12) { return false; }
	// if (array[0] != array[size - 3] || array[1] != array[size - 2] || array[2] != array[size - 1]) { return false; }
	// return true;
	// }

	public Object createLinearRing(final Object[] coordinates) {
		return null;
	}

	public Object buildRectangle(final Object[] points) {
		return null;
	}

	/**
	 * Polygons are created after ensuring that the coordinate sequence in them has been turned clockwise
	 */

	public Object createPolygon(final Object shell, final Object[] holes) {
		return null;
	}

	private Object turnClockwise(final Object ring) {
		return null;
	}

	public GamaCoordinateSequenceFactory getCoordinateSequenceFactory() {
		return COORDINATES_FACTORY;
	}

	public Object createLineString(final GamaPoint[] coordinates, final boolean copyPoints) {
		return null;
	}

}
