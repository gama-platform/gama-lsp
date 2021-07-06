/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaPoint.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import static java.lang.Math.sqrt;
import static msi.gaml.operators.Maths.round;

import msi.gama.common.geometry.Envelope3D;
//import msi.gama.common.geometry.ObjectUtils;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.IAttributed;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A mutable point in 3D, deriving from JTS Object, that serves muliple purposes (location of agents, of geometries
 * -- through GamaObjectSequence --, vectors -- see Rotation3D and AxisAngle, etc.)
 *
 * @author drogoul 11 oct. 07
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })

public class GamaPoint implements ILocation {
	public double x;
	public double y;
	public double z;

	@Override
	public GamaPoint toGamaPoint() {
		return this;
	}

	public GamaPoint() {
		x = 0.0d;
		y = 0.0d;
		z = 0.0d;
	}

	public GamaPoint(final double x, final double y) {
		setLocation(x, y, 0d);
	}

	public GamaPoint(final double x, final double y, final double z) {
		setLocation(x, y, z);
	}

	public GamaPoint(final Object coord) {
		setLocation(0d, 0d, 0d);
	}

	@Override
	public void setLocation(final ILocation al) {
		if (al == this) { return; }
		setLocation(al.getX(), al.getY(), al.getZ());
	}

	public GamaPoint setLocation(final GamaPoint al) {
		if (al == this) { return this; }
		return setLocation(al.x, al.y, al.z);
	}

	public GamaPoint setLocation(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		setZ(z);
		return this;
	}

	public void setObject(final Object c) {
		return;
	}

	public void setOrdinate(final int i, final double v) {
		return;
	}

	@Override
	public void setX(final double xx) {
		x = xx;
	}

	@Override
	public void setY(final double yy) {
		y = yy;
	}

	@Override
	public void setZ(final double zz) {
		z = Double.isNaN(zz) ? 0.0d : zz;
	}

	@Override
	@getter ("x")
	public double getX() {
		return x;
	}

	@Override
	@getter ("y")
	public double getY() {
		return y;
	}

	@Override
	@getter ("z")
	public double getZ() {
		return z;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

	@Override
	public boolean isLine() {
		return false;
	}

	@Override
	public String toString() {
		return "location[" + x + ";" + y + ";" + z + "]";
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "{" + x + "," + y + "," + z + "}";
	}

	@Override
	public GamaPoint getLocation() {
		return this;
	}

	@Override
	public String stringValue(final IScope scope) {
		return "{" + x + "," + y + "," + z + "}";
	}

	@Override
	public void add(final ILocation loc) {
		setX(x + loc.getX());
		setY(y + loc.getY());
		setZ(z + loc.getZ());
	}

	public GamaPoint add(final GamaPoint loc) {
		x += loc.x;
		y += loc.y;
		setZ(z + loc.z);
		return this;
	}

	public GamaPoint add(final double ax, final double ay, final double az) {
		x += ax;
		y += ay;
		setZ(z + az);
		return this;
	}

	public GamaPoint subtract(final GamaPoint loc) {
		x -= loc.x;
		y -= loc.y;
		setZ(z - loc.z);
		return this;
	}

	public GamaPoint multiplyBy(final double value) {
		x *= value;
		y *= value;
		setZ(z * value);
		return this;
	}

	public GamaPoint divideBy(final double value) {
		x /= value;
		y /= value;
		setZ(z / value);
		return this;
	}

	@Override
	public GamaPoint copy(final IScope scope) {
		return new GamaPoint(x, y, z);
	}

	public GamaShape getObject() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IObject#setObject(msi.gama.util.GamaObject)
	 */
	public void setObject(final IShape g) {
		setLocation(g.getLocation());
	}

	/**
	 * @see msi.gama.interfaces.IObject#getInnerObject()
	 */
	public Object getInnerObject() {
		return null;
	}

	/**
	 * @see msi.gama.interfaces.IObject#getEnvelope()
	 */
	@Override
	public Envelope3D getEnvelope() {
		return null;
	}

	@Override
	public boolean equals(final Object o) {
		return true;
	}

	public boolean equalsWithTolerance(final Object c, final double tolerance) {
		return true;
	}

	/**
	 * @see msi.gama.interfaces.IObject#covers(msi.gama.interfaces.IObject)
	 */
	@Override
	public boolean covers(final IShape g) {
		if (g.isPoint()) { return g.getLocation().equals(this); }
		return false;
	}

	/**
	 * @see msi.gama.interfaces.IObject#euclidianDistanceTo(msi.gama.interfaces.IObject)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		if (g.isPoint()) { return euclidianDistanceTo(g.getLocation()); }
		return g.euclidianDistanceTo(this);
	}

	@Override
	public double euclidianDistanceTo(final ILocation p) {
		final double dx = p.getX() - x;
		final double dy = p.getY() - y;
		final double dz = p.getZ() - z;
		return sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double euclidianDistanceTo(final GamaPoint p) {
		return 0.0;
	}

	/**
	 * @see msi.gama.interfaces.IObject#intersects(msi.gama.interfaces.IObject)
	 */
	@Override
	public boolean intersects(final IShape g) {
		if (g.isPoint()) { return g.getLocation().equals(this); }
		return g.intersects(this);
	}

	@Override
	public boolean crosses(final IShape g) {
		if (g.isPoint()) { return false; }
		return g.crosses(this);
	}

	/**
	 * @see msi.gama.interfaces.IObject#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return null;
	}

	/**
	 * @see msi.gama.interfaces.IObject#setAgent(msi.gama.interfaces.IAgent)
	 */
	@Override
	public void setAgent(final IAgent agent) {}

	/**
	 */
	public void setInnerObject(final Object point) {
		return;
	}

	/**
	 * @see msi.gama.common.interfaces.IObject#dispose()
	 */
	@Override
	public void dispose() {}

	// @Override
	// public GamaMap getAttributes() {
	// return null;
	// }

	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		return GamaMapFactory.create();
	}

	@Override
	public Object getAttribute(final String key) {
		return null;
	}

	@Override
	public void setAttribute(final String key, final Object value) {}

	@Override
	public boolean hasAttribute(final String key) {
		return false;
	}

	/**
	 * Method getGeometricalType()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometricalType()
	 */
	@Override
	public Type getGeometricalType() {
		return Type.POINT;
	}

	public GamaPoint times(final double d) {
		return new GamaPoint(x * d, y * d, z * d);
	}

	public GamaPoint dividedBy(final double d) {
		return new GamaPoint(x / d, y / d, z / d);
	}

	public GamaPoint minus(final GamaPoint other) {
		return new GamaPoint(x - other.x, y - other.y, z - other.z);
	}

	public GamaPoint minus(final double ax, final double ay, final double az) {
		return new GamaPoint(x - ax, y - ay, z - az);
	}

	public GamaPoint plus(final GamaPoint other) {
		return new GamaPoint(x + other.x, y + other.y, z + other.z);
	}

	public GamaPoint plus(final double ax, final double ay, final double az) {
		return new GamaPoint(x + ax, y + ay, z + az);
	}

	public double norm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public int hashCode() {
		return 0;
	}

	public GamaPoint normalized() {
		final double r = this.norm();
		if (r == 0d) { return new GamaPoint(0, 0, 0); }
		return new GamaPoint(this.x / r, this.y / r, this.z / r);
	}

	public GamaPoint normalize() {
		final double r = this.norm();
		if (r == 0d) { return this; }
		x = x / r;
		y = y / r;
		z = z / r;
		return this;
	}

	public GamaPoint negated() {
		return new GamaPoint(-x, -y, -z);
	}

	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}

	public final static double dotProduct(final GamaPoint v1, final GamaPoint v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public final static GamaPoint cross(final GamaPoint v1, final GamaPoint v2) {
		return new GamaPoint(v1.y * v2.z - v1.z * v2.y, v2.x * v1.z - v2.z * v1.x, v1.x * v2.y - v1.y * v2.x);
	}

	/**
	 * Method getPoints()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPoints()
	 */
	@Override
	public IList<? extends ILocation> getPoints() {
		final IList result = GamaListFactory.create(Types.POINT);
		result.add(clone());
		return result;
	}

	/**
	 * @return the point with y negated (for OpenGL, for example), without side effect on the point.
	 */
	@Override
	public GamaPoint yNegated() {
		return new GamaPoint(x, -y, z);
	}

	@Override
	public void setDepth(final double depth) {}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType getGamlType() {
		return Types.POINT;
	}

	/**
	 * Method getArea()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getArea()
	 */
	@Override
	public Double getArea() {
		return 0d;
	}

	/**
	 * Method getVolume()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getVolume()
	 */
	@Override
	public Double getVolume() {
		return 0d;
	}

	/**
	 * Method getPerimeter()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return 0;
	}

	/**
	 * Method getHoles()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getHoles()
	 */
	@Override
	public IList<GamaShape> getHoles() {
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Method getCentroid()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getCentroid()
	 */
	@Override
	public GamaPoint getCentroid() {
		return this;
	}

	/**
	 * Method getExteriorRing()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getExteriorRing()
	 */
	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return new GamaShape(this);
	}

	/**
	 * Method getWidth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getWidth()
	 */
	@Override
	public Double getWidth() {
		return 0d;
	}

	/**
	 * Method getHeight()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getHeight()
	 */
	@Override
	public Double getHeight() {
		return 0d;
	}

	/**
	 * Method getDepth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getDepth()
	 */
	@Override
	public Double getDepth() {
		return null;
	}

	/**
	 * Method getGeometricEnvelope()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometricEnvelope()
	 */
	@Override
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(this);
	}

	/**
	 * Method getGeometries()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometries()
	 */
	@Override
	public IList<? extends IShape> getGeometries() {
		return GamaListFactory.wrap(Types.GEOMETRY, new GamaShape(this));
	}

	/**
	 * Method isMultiple()
	 *
	 * @see msi.gama.metamodel.shape.IShape#isMultiple()
	 */
	@Override
	public boolean isMultiple() {
		return false;
	}

	public double getOrdinate(final int i) {
		switch (i) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
		}
		return 0d;
	}

	@Override
	public void copyShapeAttributesFrom(final IShape other) {}

	public GamaPoint orthogonal() {
		final double threshold = 0.6 * norm();
		if (threshold == 0) { return this; }
		if (Math.abs(x) <= threshold) {
			final double inverse = 1 / sqrt(y * y + z * z);
			return new GamaPoint(0, inverse * z, -inverse * y);
		} else if (Math.abs(y) <= threshold) {
			final double inverse = 1 / sqrt(x * x + z * z);
			return new GamaPoint(-inverse * z, 0, inverse * x);
		}
		final double inverse = 1 / sqrt(x * x + y * y);
		return new GamaPoint(inverse * y, -inverse * x, 0);
	}

	@Override
	public GamaPoint withPrecision(final int i) {
		return new GamaPoint(round(x, i), round(y, i), round(z, i));
	}

	@Override
	public void setGeometricalType(final Type t) {}

	@Override
	public GamaPoint clone() {
		return new GamaPoint(x, y, z);
	}

	// @Override
	// public Map<String, Object> getAttributes() {
	// return null;
	// }

	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {}

	@Override
	public void copyAttributesOf(final IAttributed source) {}

	@Override
	public IShape getGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getInnerGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGeometry(IShape g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInnerGeometry(Object intersection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
