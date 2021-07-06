/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.DynamicLineString.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import java.util.Objects;


import msi.gama.common.geometry.GeometryUtils;

/**
 * A dynamical geometry that represents a link between two IShape.
 *
 * @author drogoul
 *
 */
public class DynamicLineString extends LineString {

	// static {
	// try {
	// Field classes = Geometry.class.getDeclaredField("sortedClasses");
	// if ( classes != null ) {
	// classes.setAccessible(true);
	// Class[] value = (Class[]) classes.get(null);
	// List<Class> list = new ArrayList(value.length);
	// for ( Class c : value ) {
	// list.add(c);
	// if ( c == LineString.class ) {
	// list.add(DynamicLineString.class);
	// }
	// }
	// classes.set(null, list.toArray(new Class[] {}));
	// }
	// } catch (NoSuchFieldException | SecurityException |
	// IllegalArgumentException | IllegalAccessException e) {
	// e.printStackTrace();
	// }
	// }

	final IShape source, target;

	/**
	 * @param factory
	 * @param source,
	 *            target Should not be null !
	 */
	public DynamicLineString(final GeometryFactory factory, final IShape source, final IShape target) {
		super(null, factory);
		this.source = source;
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public String getGeometryType() {
		return "LineString";
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public Coordinate getCoordinate() {
		return GeometryUtils.toCoordinate(source.getLocation());
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public Coordinate[] getCoordinates() {
		return new Coordinate[] { GeometryUtils.toCoordinate(source.getLocation()),
				GeometryUtils.toCoordinate(target.getLocation()) };
	}

	@Override
	public CoordinateSequence getCoordinateSequence() {
		return getFactory().getCoordinateSequenceFactory().create(getCoordinates());
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public int getNumPoints() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public int getDimension() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public Geometry getBoundary() {
		return getFactory().createMultiPoint(new Point[] { getStartPoint(), getEndPoint() });
	}

	@Override
	public Point getStartPoint() {
		return getFactory().createPoint(getCoordinate());
	}

	@Override
	public Point getEndPoint() {
		return getFactory().createPoint(GeometryUtils.toCoordinate(target.getLocation()));
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public int getBoundaryDimension() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public LineString reverse() {
		return new DynamicLineString(getFactory(), target, source);
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public boolean equalsExact(final Geometry other, final double tolerance) {
		if (!(other instanceof DynamicLineString)) { return false; }
		final DynamicLineString dls = (DynamicLineString) other;
		return Objects.equals(dls.source, source) && Objects.equals(dls.target, target);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

		/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public void apply(final CoordinateFilter filter) {
		filter.filter(getCoordinate());
		filter.filter(GeometryUtils.toCoordinate(target.getLocation()));
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public void apply(final CoordinateSequenceFilter filter) {
		final CoordinateSequence points = getCoordinateSequence();
		filter.filter(points, 0);
		if (filter.isDone()) { return; }
		filter.filter(points, 1);
		if (filter.isGeometryChanged()) {
			geometryChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public void apply(final GeometryFilter filter) {
		filter.filter(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public void apply(final GeometryComponentFilter filter) {
		filter.filter(this);
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public void normalize() {}

	@Override
	public final Object clone() {
		final DynamicLineString s = new DynamicLineString(getFactory(), source, target);
		s.setUserData(getUserData());
		return s;
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	protected Envelope computeEnvelopeInternal() {
		final CoordinateSequence points = getFactory().getCoordinateSequenceFactory().create(getCoordinates());
		return points.expandEnvelope(new Envelope());
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	protected int compareToSameClass(final Object o) {
		final DynamicLineString line = (DynamicLineString) o;
		final int comparison = source.getLocation().compareTo(line.source.getLocation());
		if (comparison != 0) { return comparison; }
		return target.getLocation().compareTo(line.target.getLocation());
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	protected int compareToSameClass(final Object o, final CoordinateSequenceComparator comp) {
		final DynamicLineString line = (DynamicLineString) o;
		return comp.compare(getCoordinateSequence(), line.getCoordinateSequence());
	}

	@Override
	protected boolean isEquivalentClass(final Geometry other) {
		return other instanceof DynamicLineString;
	}

	@Override
	public boolean isRing() {
		return false;
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public Point getPointN(final int n) {
		if (n == 0) { return getFactory().createPoint(getCoordinate()); }
		if (n == 1) { return getFactory().createPoint(GeometryUtils.toCoordinate(target.getLocation())); }
		return null;
	}

	@Override
	public Coordinate getCoordinateN(final int n) {
		if (n == 0) { return getCoordinate(); }
		if (n == 1) { return GeometryUtils.toCoordinate(target.getLocation()); }
		return null;

	}

	/**
	 * @return
	 */
	public IShape getSource() {
		return source;
	}

	public IShape getTarget() {
		return target;
	}

}
