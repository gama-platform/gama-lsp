/*******************************************************************************************************
 *
 * msi.gama.common.geometry.GeometryUtils.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import static msi.gama.metamodel.shape.IShape.Type.LINESTRING;
import static msi.gama.metamodel.shape.IShape.Type.MULTILINESTRING;
import static msi.gama.metamodel.shape.IShape.Type.MULTIPOINT;
import static msi.gama.metamodel.shape.IShape.Type.NULL;
import static msi.gama.metamodel.shape.IShape.Type.POINT;
import static msi.gama.metamodel.shape.IShape.Type.POLYGON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import msi.gama.common.interfaces.IEnvelopeComputer;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IList;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Files;
import msi.gaml.operators.Graphs;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial.Operators;
import msi.gaml.operators.Spatial.ThreeD;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;

/**
 * The class GamaGeometryUtils.
 *
 * @author drogoul
 * @since 14 dec. 2011
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GeometryUtils {

	public static GamaPoint toCoordinate(final ILocation l) {
		return l.toGamaPoint();
	}

	private static List<IEnvelopeComputer> envelopeComputers = new ArrayList<>();

	public static void addEnvelopeComputer(final IEnvelopeComputer ec) {
		envelopeComputers.add(ec);
	}

	public final static GamaGeometryFactory GEOMETRY_FACTORY = new GamaGeometryFactory();
	public final static Object PREPARED_GEOMETRY_FACTORY = null;

	public static Double distanceOnPolyline(final IShape line, final GamaPoint pt1, final GamaPoint pt2) {
		return 0.0;
	}

	public static GamaPoint pointInGeom(final Object geom, final RandomUtils rand) {
		return null;
	}

	public static GamaPoint pointInGeom(final IShape shape, final RandomUtils rand) {
		if (shape == null) return null;
		return pointInGeom(shape.getInnerGeometry(), rand);
	}

	private static Object[] minimiseLength(final Object[] coords) {
		return null;
	}

	public static int nbCommonPoints(final Object p1, final Object p2) {
		return 0;
	}

	public static Object[] extractPoints(final IShape triangle, final Set<IShape> connectedNodes) {
		return null;
	}

	public static Object extractPoints(final IShape triangle1, final IShape triangle2) {
		return null;
	}

	public static IList<IShape> hexagonalGridFromGeom(final IShape geom, final int nbRows, final int nbColumns) {
		return null;
	}

	public static IList<IShape> squareDiscretization(final Object geom, final int nb_squares, final boolean overlaps,
			final double coeff_precision) {
		return null;
	}

	public static IList<IShape> discretization(final Object geom, final double size_x, final double size_y,
			final boolean overlaps) {
		return discretization(geom, size_x, size_y, overlaps, null);
	}

	public static IList<IShape> discretization(final Object geom, final double size_x, final double size_y,
			final boolean overlaps, final List<IShape> borders) {
		return null;
	}

	public static IList<IShape> geometryDecomposition(final IShape geom, final double x_size, final double y_size) {
		return null;
	}

	public static IList<IShape> voronoi(final IScope scope, final IList<GamaPoint> points) {
		return null;
	}

	public static IList<IShape> voronoi(final IScope scope, final IList<GamaPoint> points, final IShape clip) {
		return null;
	}

	public static IList<IShape> triangulation(final IScope scope, final IList<IShape> lines) {
		return null;
	}

	public static IList<IShape> triangulation(final IScope scope, final Object geom,
			final double toleranceTriangulation, final double toleranceClip, final boolean approxClipping) {
		return null;
	}

	private static IList<IShape> filterGeoms(final Object geom, final Object clip, final double sizeTol,
			final boolean approxClipping) {
		return null;
	}

	public static void iterateOverTriangles(final Object polygon, final Consumer<Object> action) {
		return;
	}

	public static List<Object> squeletisation(final IScope scope, final Object geom,
			final double toleranceTriangulation, final double toleranceClip, final boolean approxClipping) {
		return null;
	}

	public static Object buildGeometryJTS(final List<List<List<ILocation>>> listPoints) {
		return null;
	}

	private static Object buildPoint(final List<List<ILocation>> listPoints) {
		return null;
	}

	public static Object buildGeometryCollection(final List<IShape> geoms) {
		return null;
	}

	private static Object buildLine(final List<List<ILocation>> listPoints) {
		return null;
	}

	private static Object buildPolygon(final List<List<ILocation>> listPoints) {
		return null;
	}

	private static IShape.Type geometryType(final List<List<List<ILocation>>> listPoints) {
		final int size = listPoints.size();
		if (size == 0) return NULL;
		if (size == 1) return geometryTypeSimp(listPoints.get(0));
		final IShape.Type type = geometryTypeSimp(listPoints.get(0));
		switch (type) {
			case POINT:
				return MULTIPOINT;
			case LINESTRING:
				return MULTILINESTRING;
			case POLYGON:
				return POLYGON;
			default:
				return NULL;
		}
	}

	private static IShape.Type geometryTypeSimp(final List<List<ILocation>> listPoints) {
		if (listPoints.isEmpty() || listPoints.get(0).isEmpty()) return NULL;
		final List<ILocation> list0 = listPoints.get(0);
		final int size0 = list0.size();
		if (size0 == 1 || size0 == 2 && list0.get(0).equals(list0.get(listPoints.size() - 1))) return POINT;
		if (!list0.get(0).equals(list0.get(listPoints.size() - 1)) || size0 < 3) return LINESTRING;
		return POLYGON;
	}

	public static IList<GamaPoint> locsOnGeometry(final Object geom, final Double distance) {
		return null;
	}

	public static IList<GamaPoint> locsAlongGeometry(final Object geom, final List<Double> rates) {
		return null;
	}

	// ---------------------------------------------------------------------------------------------
	// Thai.truongminh@gmail.com
	// Created date:24-Feb-2013: Process for SQL - MAP type
	// Modified: 03-Jan-2014

	public static Envelope3D computeEnvelopeFrom(final IScope scope, final Object obj) {
		return null;
	}

	private static IList<IShape> split_at(final Object g, final GamaPoint pt) {
		return null;
	}

	public static IList<IShape> split_at(final IShape geom, final ILocation pt) {
		return split_at(geom.getInnerGeometry(), pt.toGamaPoint());
	}

	/**
	 * @param intersect
	 * @return
	 */
	public static Type getTypeOf(final Object g) {
		return null;
	}

	/**
	 * @param ownScope
	 * @param innerGeometry
	 * @param param
	 * @return
	 */
	public static IShape smooth(final Object geom, final double fit) {
		return null;
	}

	public static ICoordinates getContourCoordinates(final Object g) {
		return null;
	}

	public static GamaPoint[] getPointsOf(final IShape shape) {
		final Object g = shape.getInnerGeometry();
		return getContourCoordinates(g).toCoordinateArray();
	}

	public static GamaPoint[] getPointsOf(final Object g) {
		return getContourCoordinates(g).toCoordinateArray();
	}

	public static GamaPoint getFirstPointOf(final IShape shape) {
		return null;
	}

	public static GamaPoint getLastPointOf(final IShape shape) {
		return null;
	}

	/**
	 * Applies a GeometryComponentFilter to internal geometries. Concerns the geometries contained in multi-geometries,
	 * and the holes in polygons. Limited to one level (i.e. holes in polygons in a MultiPolygon will not be visited)
	 *
	 * @param g
	 *            the geometry to visit
	 * @param f
	 *            the filter to apply
	 */
	public static void applyToInnerGeometries(final Object g, final Object f) {
		return;
	}

	public static void translate(final Object geometry, final GamaPoint previous, final GamaPoint location) {
		final double dx = location.x - previous.x;
		final double dy = location.y - previous.y;
		final double dz = location.z - previous.z;
		translate(geometry, dx, dy, dz);
	}

	public static void translate(final Object geometry, final double dx, final double dy, final double dz) {
		return;
	}

	public static void rotate(final Object geometry, final GamaPoint center, final AxisAngle rotation) {
		return;
	}

	public static ICoordinates getYNegatedCoordinates(final Object geom) {
		return getContourCoordinates(geom).yNegated();
	}

	public static int getHolesNumber(final Object p) {
		return 0;
	}

	public static Object geometryCollectionManagement(final Object gjts) {
		return null;
	}

	public static Object cleanGeometry(final Object g) {
		return null;
	}
}
