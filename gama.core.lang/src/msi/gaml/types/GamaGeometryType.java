/*******************************************************************************************************
 *
 * msi.gaml.types.GamaGeometryType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import static msi.gama.metamodel.shape.IShape.Type.BOX;
import static msi.gama.metamodel.shape.IShape.Type.CONE;
import static msi.gama.metamodel.shape.IShape.Type.CUBE;
import static msi.gama.metamodel.shape.IShape.Type.CYLINDER;
import static msi.gama.metamodel.shape.IShape.Type.LINECYLINDER;
import static msi.gama.metamodel.shape.IShape.Type.PLAN;
import static msi.gama.metamodel.shape.IShape.Type.POLYHEDRON;
import static msi.gama.metamodel.shape.IShape.Type.POLYPLAN;
import static msi.gama.metamodel.shape.IShape.Type.PYRAMID;
import static msi.gama.metamodel.shape.IShape.Type.SPHERE;
import static msi.gama.metamodel.shape.IShape.Type.TEAPOT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.DynamicLineString;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Spatial;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@type (
		name = IKeyword.GEOMETRY,
		id = IType.GEOMETRY,
		wraps = { IShape.class, GamaShape.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.GEOMETRY },
		doc = @doc ("Represents geometries, i.e. the support for the shapes of agents and all the spatial operations in GAMA."))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGeometryType extends GamaType<IShape> {

	public static Object SHAPE_READER = null;

	@Override
	public IShape cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static IShape staticCast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	private static boolean isPoints(final IScope scope, final IContainer obj) {
		for (final Object o : obj.iterable(scope)) {
			if (!(o instanceof ILocation)) { return false; }
		}
		return true;
	}

	@Override
	public GamaShape getDefault() {
		return null;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public IType getKeyType() {
		return Types.STRING;
	}

	//
	// @Override
	// public boolean hasContents() {
	// return true;
	// }

	@Override
	public boolean isFixedLength() {
		return false;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	/**
	 * Builds a (cleansed) polygon from a list of points. The input points must be valid to create a linear ring (first
	 * point and last point are duplicated). It is the responsibility of the caller to assure the validity of the input
	 * parameter. Update: the coordinate sequence is now validated before creating the polygon, and any necessary point
	 * is added.
	 *
	 * @param points
	 * @return
	 */
	public static IShape buildPolygon(final List<? extends IShape> points) {
		return null;
	}

	// A.G 28/05/2015 ADDED for gamanalyser
	public static IShape buildMultiPolygon(final List<List<IShape>> lpoints) {
		return null;
	}

	public static IShape buildTriangle(final double base, final double height, final ILocation location) {
		return null;
	}

	public static IShape buildTriangle(final double side_size, final ILocation location) {
		return null;
	}

	public static IShape buildRectangle(final double width, final double height, final ILocation location) {
		return null;
	}

	/**
	 * Builds a (cleansed) polyhedron from a list of points and a given depth. The input points must be valid to create
	 * a linear ring (first point and last point are duplicated). It is the responsible of the caller to assure the
	 * validity of the input parameter. Update: the coordinate sequence is now validated before creating the polygon,
	 * and any necessary point is added.
	 *
	 * @param points
	 * @return
	 */
	public static IShape buildPolyhedron(final List<IShape> points, final Double depth) {
		final IShape g = buildPolygon(points);
		// if (!Spatial.ThreeD.isClockwise(null, g)) {
		// g = Spatial.ThreeD.changeClockwise(null, g);
		// }
		g.setDepth(depth);
		g.setGeometricalType(POLYHEDRON);
		return g;
	}

	public static IShape buildLine(final IShape location2) {
		return buildLine(new GamaPoint(), location2);
	}

	public static IShape buildLine(final IShape location1, final IShape location2) {
		return null;
	}

	public static IShape buildLineCylinder(final IShape location1, final IShape location2, final double radius) {
		final IShape g = buildLine(location1, location2);
		g.setDepth(radius);
		g.setGeometricalType(LINECYLINDER);
		return g;
	}

	public static IShape buildPlan(final IShape location1, final IShape location2, final Double depth) {
		final IShape g = buildLine(location1, location2);
		g.setDepth(depth);
		g.setGeometricalType(PLAN);
		return g;
	}

	public static IShape buildPolyline(final List<IShape> points) {
		return null;
	}

	public static IShape buildPolylineCylinder(final List<IShape> points, final double radius) {
		final IShape g = buildPolyline(points);
		g.setDepth(radius);
		g.setGeometricalType(LINECYLINDER);
		return g;
	}

	public static IShape buildPolyplan(final List<IShape> points, final Double depth) {
		final IShape g = buildPolyline(points);
		g.setDepth(depth);
		g.setGeometricalType(POLYPLAN);
		return g;
	}

	public static GamaShape createPoint(final IShape location) {
		return null;
	}

	public static IShape buildSquare(final double side_size, final ILocation location) {
		return buildRectangle(side_size, side_size, location);
	}

	public static IShape buildCube(final double side_size, final ILocation location) {

		final IShape g = buildRectangle(side_size, side_size, location);
		g.setDepth(side_size);
		g.setGeometricalType(CUBE);
		return g;

	}

	public static IShape buildBox(final double width, final double height, final double depth,
			final ILocation location) {
		final IShape g = buildRectangle(width, height, location);
		g.setDepth(depth);
		g.setGeometricalType(BOX);
		return g;
	}

	public static IShape buildHexagon(final double size, final double x, final double y) {
		return buildHexagon(size, new GamaPoint(x, y));
	}

	public static IShape buildHexagon(final double size, final ILocation location) {
		return buildHexagon(size, size, location);
	}

	public static IShape buildHexagon(final double sizeX, final double sizeY, final ILocation location) {
		return null;
	}

	public static IShape buildCircle(final double radius, final ILocation location) {
		return null;
	}

	public static IShape buildEllipse(final double xRadius, final double yRadius, final GamaPoint location) {
		return null;
	}

	public static IShape buildSquircle(final double xRadius, final double power, final GamaPoint location) {
		return null;
	}

	/**
	 *
	 * @param xRadius
	 * @param heading
	 *            in decimal degrees
	 * @param amplitude
	 *            in decimal degrees
	 * @param filled
	 * @param location
	 * @return
	 */
	public static IShape buildArc(final double xRadius, final double heading, final double amplitude,
			final boolean filled, final GamaPoint location) {
		return null;
	}

	public static IShape buildCylinder(final double radius, final double depth, final ILocation location) {
		final IShape g = buildCircle(radius, location);
		g.setDepth(depth);
		g.setGeometricalType(CYLINDER);
		return g;
	}

	// FIXME: Be sure that a buffer on a sphere returns a sphere.
	public static IShape buildSphere(final double radius, final ILocation location) {
		final IShape g = buildCircle(radius, location);
		g.setDepth(radius);
		g.setGeometricalType(SPHERE);
		return g;
	}

	public static IShape buildCone3D(final double radius, final double depth, final ILocation location) {
		final IShape g = buildCircle(radius, location);
		g.setDepth(depth);
		g.setGeometricalType(CONE);
		return g;
	}

	public static IShape buildTeapot(final double size, final ILocation location) {
		final IShape g = buildCircle(size, location);
		g.setDepth(size);
		g.setGeometricalType(TEAPOT);
		return g;
	}

	public static IShape buildPyramid(final double side_size, final ILocation location) {
		final IShape g = buildRectangle(side_size, side_size, location);
		g.setDepth(side_size);
		g.setGeometricalType(PYRAMID);
		return g;
	}

	private static double theta = Math.tan(0.423d);

	public static IShape buildArrow(final GamaPoint head, final double size) {
		return buildArrow(new GamaPoint(), head, size, size, true);
	}

	public static IShape buildArrow(final GamaPoint tail, final GamaPoint head, final double arrowWidth,
			final double arrowLength, final boolean closed) {
		final IList points = GamaListFactory.createWithoutCasting(Types.POINT, head);
		// build the line vector
		final GamaPoint vecLine = head.minus(tail);
		// build the arrow base vector - normal to the line
		GamaPoint vecLeft = new GamaPoint(-vecLine.y, vecLine.x);
		if (vecLine.y == 0 && vecLine.x == 0) {
			vecLeft = new GamaPoint(-vecLine.z, 0, 0);
		}
		// setup length parameters
		final double fLength = vecLine.norm();
		final double th = arrowWidth / (2.0d * fLength);
		final double ta = arrowLength / (2.0d * theta * fLength);
		// find the base of the arrow
		final GamaPoint base = head.minus(vecLine.times(ta));
		// build the points on the sides of the arrow
		if (closed) {
			points.add(base.plus(vecLeft.times(th)));
		} else {
			points.add(0, base.plus(vecLeft.times(th)));
		}
		points.add(base.minus(vecLeft.times(th)));
		return closed ? buildPolygon(points) : buildPolyline(points);
	}

	public static GamaShape geometriesToGeometry(final IScope scope, final IContainer<?, ? extends IShape> ags)
			throws GamaRuntimeException {
		return null;
	}

	public static GamaShape pointsToGeometry(final IScope scope, final IContainer<?, ILocation> coordinates)
			throws GamaRuntimeException {
		if (coordinates != null && !coordinates.isEmpty(scope)) {
		return null;
		}
		return null;
	}

	public static GamaShape buildLink(final IScope scope, final IShape source, final IShape target) {
		return null;
	}

	public static IShape pairToGeometry(final IScope scope, final GamaPair p) throws GamaRuntimeException {
		final IShape first = staticCast(scope, p.first(), null, false);
		if (first == null) { return null; }
		final IShape second = staticCast(scope, p.last(), null, false);
		if (second == null) { return null; }
		return buildLink(scope, first, second);
	}

	public static IShape buildMultiGeometry(final IList<IShape> shapes) {
		return null;
	}

	public static IShape buildMultiGeometry(final IShape... shapes) {
		try (final Collector.AsList<IShape> list = Collector.getList()) {
			for (final IShape shape : shapes) {
				if (shape != null) {
					list.add(shape);
				}
			}
			return buildMultiGeometry(list.items());
		}
	}

	public static IShape buildCross(final Double xRadius, final Double width, final GamaPoint location) {
		return null;
	}

	// /////////////////////// 3D Shape (Not yet implemented in 3D (e.g a Sphere
	// is displayed as a
	// sphere but is a JTS circle) /////////////////////////////

}
