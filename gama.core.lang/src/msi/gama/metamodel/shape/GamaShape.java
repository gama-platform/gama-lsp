/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaShape.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.rotate;
import static msi.gama.common.geometry.GeometryUtils.translate;
import static msi.gama.util.GamaListFactory.create;
import static msi.gaml.types.Types.POINT;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.IAttributed;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Maths;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 25 ao�t 2010
 *
 *
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaShape implements IShape {

	class ShapeData {
		private Double depth;
		private Type type;
	}

	protected Object geometry;
	private IAgent agent;
	protected IMap<String, Object> attributes;

	public GamaShape(final Object geom) {
		setInnerGeometry(geom);
	}

	@Override
	public IType getGamlType() {
		return Types.GEOMETRY;
	}

	public GamaShape(final Envelope3D env) {
		this(env == null ? null : env.toGeometry());
	}

	public GamaShape(final IShape geom) {
		this(geom, null);

	}

	/**
	 * Creates a GamaShape from a source and a (optional) geometry. If the geometry
	 * is null, the geometry of the source is used. In any case, we copy its
	 * attributes if present and if copyAttributes is true
	 *
	 * @param source
	 * @param geom
	 * @param copyAttributes
	 */

	public GamaShape(final IShape source, final Object geom) {
		return;
	}

	/**
	 * This is where the attributes of this shape and the attributes of an incoming
	 * shape are mixed. The default strategy is to copy all the attributes to this
	 *
	 * @param source
	 */
	private void mixAttributes(final IShape source) {
		if (source == null)
			return;
		// final GamaMap<String, Object> attr = (GamaMap<String, Object>)
		// source.getAttributes();
		copyShapeAttributesFrom(source);
		if (source instanceof GamaShape) {
			final GamaShape shape = (GamaShape) source;
			if (shape.attributes != null) {
				getOrCreateAttributes();
				shape.attributes.forEach((key, val) -> {
					if (val != source) {
						attributes.put(key, val);
					}
				});
			}
		} else {
			// if (attr == null) { return; }
			source.forEachAttribute((key, val) -> {
				if (val != source) {
					setAttribute(key, val);
				}
				return true;
			});
			// for (final Map.Entry<String, Object> entry : attr.entrySet()) {
			// if (entry.getValue() != source) {
			// setAttribute(entry.getKey(), entry.getValue());
			// }
			// }
		}
	}

	@Override
	public void copyAttributesOf(final IAttributed source) {
		if (source instanceof GamaShape) {
			final GamaShape shape = (GamaShape) source;
			if (shape.attributes != null) {
				getOrCreateAttributes();
				attributes.putAll(shape.attributes);
			}
		} else {
			IShape.super.copyAttributesOf(source);
		}

	}

	/**
	 * Same as above, but applies a (optional) rotation around a given vector and
	 * (optional) translation to the geometry
	 *
	 * @param source      cannot be null
	 * @param geom        can be null
	 * @param rotation    can be null, expressed in degrees
	 * @param newLocation can be null
	 */

	public GamaShape(final IShape source, final Object geom, final AxisAngle rotation, final ILocation newLocation) {
		this(source, geom);
		if (!isPoint() && rotation != null) {
			Double normalZ = null;
			if (is3D()) {
				normalZ = getContourCoordinates(geometry).getNormal(true).z;
			}
			rotate(geometry, getLocation(), rotation);
			if (normalZ != null) {
				final Double normalZ2 = getContourCoordinates(geometry).getNormal(true).z;
				if (normalZ > 0 && normalZ2 < 0) {
					setDepth(-getDepth());
				}
			}
		}
		if (newLocation != null) {
			setLocation(newLocation);
		}
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by specifying
	 * a bounding box or a set of coefficients.
	 *
	 * @param source        cannot be null
	 * @param geom          can be null
	 * @param rotation      can be null, expressed in degrees
	 * @param newLocation   can be null
	 * @param isBoundingBox indicates whether the previous parameter should be
	 *                      considered as an absolute bounding box (width, height, )
	 *                      or as a set of coefficients.
	 */
	public GamaShape(final IShape source, final Object geom, final AxisAngle rotation, final ILocation newLocation,
			final Scaling3D bounds, final boolean isBoundingBox) {
		return;
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by a given
	 * coefficient
	 *
	 * @param source      cannot be null
	 * @param geom        can be null
	 * @param rotation    can be null, expressed in degrees
	 * @param newLocation can be null
	 */
	public GamaShape(final IShape source, final Object geom, final AxisAngle rotation, final ILocation newLocation,
			final Double scaling) {
		return;
	}

	@Override
	public boolean isMultiple() {
		return true;
	}

	public boolean is3D() {
		return getDepth() != null;
	}

	@Override
	public IList<GamaShape> getGeometries() {
		return null;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

	@Override
	public boolean isLine() {
		return true;
	}

	@Override
	public String stringValue(final IScope scope) {
		return "";
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public GamaPoint getLocation() {
		return null;
	}

	@Override
	public void setLocation(final ILocation l) {
		return;
	}

	public GamaShape translatedTo(final IScope scope, final ILocation target) {
		final GamaShape result = copy(scope);
		result.setLocation(target);
		return result;
	}

	public final static Object pl = null;

	@Override
	public GamaShape getGeometry() {
		return this;
	}

	@Override
	public Double getArea() {
		return null;
	}

	@Override
	public Double getVolume() {
		final Double d = getDepth();
		if (d == 0)
			return 0d;
		else {
			final Type shapeType = getGeometricalType();
			// TODO : should put any specific shape volume calculation here !!!
			switch (shapeType) {
			case SPHERE:
				return 4 / (double) 3 * Maths.PI * Maths.pow(getWidth() / 2.0, 3);
			case CONE:
				return 1 / (double) 3 * Maths.PI * Maths.pow(getWidth() / 2.0, 2) * d;
			case PYRAMID:
				return Maths.pow(getWidth(), 2) * d / 3;
			case THREED_FILE:
			case NULL:
				final Envelope3D env3D = getEnvelope();
				return env3D == null ? Envelope3D.of(this.getGeometry().getInnerGeometry()).getVolume()
						: env3D.getVolume();
			default:
				return getArea() * d;
			}
		}
	}

	@Override
	public double getPerimeter() {
		return 0.0;
	}

	@Override
	public IList<GamaShape> getHoles() {
		return null;
	}

	@Override
	public GamaPoint getCentroid() {
		return null;
	}

	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return null;
	}

	private ShapeData getData(final boolean createIt) {
		return null;
	}

	@Override
	public Double getWidth() {
		return null;
	}

	@Override
	public Double getHeight() {
		return null;
	}

	@Override
	public Double getDepth() {
		final ShapeData data = getData(false);
		return data == null ? null : data.depth;
	}

	@Override
	public void setDepth(final double depth) {
		final ShapeData data = getData(true);
		if (data != null) {
			data.depth = depth;
		}
	}

	@Override
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getEnvelope());
	}

	@Override
	public IList<? extends ILocation> getPoints() {
		return null;
	}

	@Override
	public Envelope3D getEnvelope() {
		if (geometry == null)
			return null;
		return Envelope3D.of(this);
	}

	@Override
	public IAgent getAgent() {
		return agent;
	}

	@Override
	public void setAgent(final IAgent a) {
		agent = a;
	}

	@Override
	public void setInnerGeometry(final Object geom) {
		return;
	}

	@Override
	public void setGeometry(final IShape geom) {
		if (geom == null || geom == this)
			return;
		setInnerGeometry(geom.getInnerGeometry());
		mixAttributes(geom);
	}

	private double computeAverageZOrdinate() {
		return 0.0;
	}

	@Override
	public void dispose() {
		agent = null;
		if (attributes != null) {
			attributes.clear();
		}
	}

	@Override
	public boolean equals(final Object o) {
		return true;
	}

	@Override
	public int hashCode() {
		if (geometry == null)
			return 0;
		return geometry.hashCode();
		// return super.hashCode();
		// if (geomtry == null) return s
		// return GeometryUtils.getContourCoordinates(geometry)..
		// return geometry == null ? super.hashCode() : geometry.hashCode();
	}

	@Override
	public Object getInnerGeometry() {
		return geometry;
	}

	@Override
	public GamaShape copy(final IScope scope) {
		return null;
	}

	/**
	 *
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		return true;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		return 0.0;
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		return 0.0;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return true;
	}

	@Override
	public boolean crosses(final IShape g) {
		return true;
	}

	/**
	 * Used when the geometry is not affected to an agent and directly accessed by
	 * 'read' or 'get' operators. Can be used in Java too, of course, to retrieve
	 * any value stored in the shape
	 *
	 * @param s
	 * @return the corresponding value of the attribute named 's' in the feature, or
	 *         null if it is not present
	 */
	@Override
	public Object getAttribute(final String s) {
		if (attributes == null)
			return null;
		return attributes.get(s);
	}

	@Override
	public void setAttribute(final String key, final Object value) {
		getOrCreateAttributes().put(key, value);
	}

	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		if (attributes == null) {
			attributes = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		}
		return attributes;
	}

	// @Override
	// public GamaMap getAttributes() {
	// return attributes;
	// }

	@Override
	public boolean hasAttribute(final String key) {
		return attributes != null && attributes.containsKey(key);
	}

	/**
	 * Method getGeometricalType()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometricalType()
	 */
	@Override
	public Type getGeometricalType() {
		return null;
	}

	/**
	 * Invoked when a geometrical primitive undergoes an operation (like minus(),
	 * plus()) that makes it change
	 */
	public void losePredefinedProperty() {
		if (THREED_TYPES.contains(getGeometricalType())) {
			setGeometricalType(Type.POLYHEDRON);
		}
	}

	@Override
	public void setGeometricalType(final Type t) {
		final ShapeData data = getData(true);
		if (data != null) {
			data.type = t;
		}
	}

	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {
		if (attributes == null)
			return;
		attributes.forEachPair(visitor);
	}

}
