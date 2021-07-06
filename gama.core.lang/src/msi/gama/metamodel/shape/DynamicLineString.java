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
public class DynamicLineString {

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
	 * @param source, target Should not be null !
	 */
	public DynamicLineString(final Object factory, final IShape source, final IShape target) {
		this.source = source;
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public String getGeometryType() {
		return "LineString";
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public Object getCoordinate() {
		return GeometryUtils.toCoordinate(source.getLocation());
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public Object[] getCoordinates() {
		return null;
	}

	public Object getCoordinateSequence() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public int getNumPoints() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public boolean isEmpty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public int getDimension() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public Object getBoundary() {
		return null;
	}

	public Object getStartPoint() {
		return null;
	}

	public Object getEndPoint() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public int getBoundaryDimension() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public Object reverse() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public boolean equalsExact(final Object other, final double tolerance) {
		if (!(other instanceof DynamicLineString)) {
			return false;
		}
		final DynamicLineString dls = (DynamicLineString) other;
		return Objects.equals(dls.source, source) && Objects.equals(dls.target, target);
	}

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

	public void apply(final Object filter) {
		return;
	}

	public boolean isSimple() {
		return true;
	}

	public boolean isValid() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	public void normalize() {
	}

	public final Object clone() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	protected Object computeEnvelopeInternal() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	protected int compareToSameClass(final Object o) {
		final DynamicLineString line = (DynamicLineString) o;
		final int comparison = source.getLocation().compareTo(line.source.getLocation());
		if (comparison != 0) {
			return comparison;
		}
		return target.getLocation().compareTo(line.target.getLocation());
	}

	/*
	 * (non-Javadoc)
	 *
	 */

	protected int compareToSameClass(final Object o, final Object comp) {
		return 0;
	}

	protected boolean isEquivalentClass(final Object other) {
		return other instanceof DynamicLineString;
	}

	public boolean isRing() {
		return false;
	}

	public boolean isClosed() {
		return false;
	}

	public Object getPointN(final int n) {
		if (n == 0) {
			return null;
		}
		if (n == 1) {
			return null;
		}
		return null;
	}

	public Object getCoordinateN(final int n) {
		if (n == 0) {
			return getCoordinate();
		}
		if (n == 1) {
			return GeometryUtils.toCoordinate(target.getLocation());
		}
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
