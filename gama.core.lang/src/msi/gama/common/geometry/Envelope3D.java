/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Envelope3D.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gaml.operators.Comparison;
import msi.gaml.types.GamaGeometryType;

// import org.opengis.geometry.MismatchedDimensionException;

/**
 * A 3D envelope that extends the 2D JTS Envelope.
 *
 *
 * @author Niels Charlier
 * @adapted for GAMA by A. Drogoul
 *
 */
public class Envelope3D implements IDisposable {

	private final static PoolUtils.ObjectPool<Envelope3D> POOL = PoolUtils.create("Envelope 3D", true,
			() -> new Envelope3D(), (from, to) -> to.set(from), null);

	public static final Envelope3D EMPTY = create();

	public static Envelope3D create() {
		return POOL.get();
	}

	public static Envelope3D of(final IShape s) {
		return of(s.getInnerGeometry());
	}

	public static Envelope3D of(final ILocation s) {
		return null;
	}

	public static Envelope3D withYNegated(final Object e) {
		return null;
	}

	public static Envelope3D of(final Object p) {
		final Envelope3D env = create();
		env.init(p);
		return env;
	}

	public static Envelope3D of(final double x1, final double x2, final double y1, final double y2, final double z1,
			final double z2) {
		final Envelope3D env = create();
		env.init(x1, x2, y1, y2, z1, z2);
		return env;
	}

	@Override
	public void dispose() {
		setToNull();
		POOL.release(this);
	}

	/**
	 * Serial number for compatibility with different versions.
	 */
	private static final long serialVersionUID = -3188702602373537163L;

	/**
	 * the minimum z-coordinate
	 */
	private double minz;

	/**
	 * the maximum z-coordinate
	 */
	private double maxz;

	/**
	 * Initialize an <code>Envelope</code> for a region defined by maximum and
	 * minimum values.
	 *
	 * @param x1 the first x-value
	 * @param x2 the second x-value
	 * @param y1 the first y-value
	 * @param y2 the second y-value
	 * @param z1 the first z-value
	 * @param z2 the second z-value
	 */
	public void init(final double x1, final double x2, final double y1, final double y2, final double z1,
			final double z2) {
		return;
	}

	/**
	 * Initialize an <code>Envelope</code> to a region defined by two Coordinates.
	 *
	 * @param p1 the first Coordinate
	 * @param p2 the second Coordinate
	 */
	public void init(final Object p1, final Object p2) {
		return;
	}

	public void init(final Object env) {
		return;
	}

	/**
	 * Initialize an <code>Envelope</code> from an existing 3D Envelope.
	 *
	 * @param env the 3D Object to initialize from
	 */
	public void init(final Envelope3D env) {
		return;
	}

	private Envelope3D set(final Envelope3D env) {
		init(env);
		return this;
	}

	/**
	 * Returns the maximal dimension of the envelope
	 */

	public double getLargestDimension() {
		return 0.0;
	}

	/**
	 * Makes this <code>Envelope</code> a "null" envelope, that is, the envelope of
	 * the empty geometry.
	 */
	public void setToNull() {
		return;
	}

	/**
	 * Returns the difference between the maximum and minimum z values.
	 *
	 * @return max z - min z, or 0 if this is a null <code>Envelope</code>
	 */
	public double getDepth() {
		return 0.0;
	}

	/**
	 * Returns the <code>Envelope</code>s minimum z-value. min z > max z indicates
	 * that this is a null <code>Envelope</code>.
	 *
	 * @return the minimum z-coordinate
	 */
	public double getMinZ() {
		return minz;
	}

	/**
	 * Returns the <code>Envelope</code>s maximum z-value. min z > max z indicates
	 * that this is a null <code>Envelope</code>.
	 *
	 * @return the maximum z-coordinate
	 */
	public double getMaxZ() {
		return maxz;
	}

	/**
	 * Gets the volume of this envelope.
	 *
	 * @return the volume of the envelope
	 * @return 0.0 if the envelope is null
	 */
	public double getVolume() {
		return 0.0;
	}

	/**
	 * Gets the minimum extent of this envelope across all three dimensions.
	 *
	 * @return the minimum extent of this envelope
	 */
	public double minExtent() {
		return 0.0;
	}

	/**
	 * Gets the maximum extent of this envelope across all three dimensions.
	 *
	 * @return the maximum extent of this envelope
	 */
	public double maxExtent() {
		return 0.0;
	}

	/**
	 * Expands this envelope by a given distance in all directions. Both positive
	 * and negative distances are supported.
	 *
	 * @param distance the distance to expand the envelope
	 */
	public void expandBy(final double distance) {
		expandBy(distance, distance, distance);
	}

	/**
	 * Expands this envelope by a given distance in all directions. Both positive
	 * and negative distances are supported.
	 *
	 * @param deltaX the distance to expand the envelope along the the X axis
	 * @param deltaY the distance to expand the envelope along the the Y axis
	 */
	public void expandBy(final double deltaX, final double deltaY, final double deltaZ) {
		return;
	}

	/**
	 * Enlarges this <code>Envelope</code> so that it contains the given point. Has
	 * no effect if the point is already on or within the envelope.
	 *
	 * @param x the value to lower the minimum x to or to raise the maximum x to
	 * @param y the value to lower the minimum y to or to raise the maximum y to
	 * @param z the value to lower the minimum z to or to raise the maximum z to
	 */
	public void expandToInclude(final double x, final double y, final double z) {
		return;
	}

	/**
	 * Translates this envelope by given amounts in the X and Y direction. Returns
	 * the envelope
	 *
	 * @param transX the amount to translate along the X axis
	 * @param transY the amount to translate along the Y axis
	 * @param transZ the amount to translate along the Z axis
	 */
	public Envelope3D translate(final double transX, final double transY, final double transZ) {
		return null;
	}

	/**
	 * Computes the coordinate of the centre of this envelope (as long as it is
	 * non-null
	 *
	 * @return the centre coordinate of this envelope <code>null</code> if the
	 *         envelope is null
	 */
	public GamaPoint centre() {
		return null;
	}

	/**
	 * Check if the point <code>p</code> overlaps (lies inside) the region of this
	 * <code>Envelope</code>.
	 *
	 * @param p the <code>Coordinate</code> to be tested
	 * @return <code>true</code> if the point overlaps this <code>Envelope</code>
	 */
	public boolean intersects(final Object p) {
		return true;
	}

	/**
	 * Check if the point <code>(x, y)</code> overlaps (lies inside) the region of
	 * this <code>Envelope</code>.
	 *
	 * @param x the x-ordinate of the point
	 * @param y the y-ordinate of the point
	 * @param z the z-ordinate of the point
	 * @return <code>true</code> if the point overlaps this <code>Envelope</code>
	 */
	protected boolean intersects(final double x, final double y, final double z) {
		return true;
	}

	/**
	 * Tests if the given point lies in or on the envelope.
	 *
	 * @param x the x-coordinate of the point which this <code>Envelope</code> is
	 *          being checked for containing
	 * @param y the y-coordinate of the point which this <code>Envelope</code> is
	 *          being checked for containing
	 * @return <code>true</code> if <code>(x, y)</code> lies in the interior or on
	 *         the boundary of this <code>Envelope</code>.
	 */
	protected boolean covers(final double x, final double y, final double z) {
		return true;
	}

	/**
	 * Tests if the <code>Envelope other</code> lies wholely inside this
	 * <code>Envelope</code> (inclusive of the boundary).
	 *
	 * @param other the <code>Envelope</code> to check
	 * @return true if this <code>Envelope</code> covers the <code>other</code>
	 */
	public boolean covers(final Object other) {
		return true;
	}

	/**
	 * Computes the distance between this and another <code>Envelope</code>. The
	 * distance between overlapping Envelopes is 0. Otherwise, the distance is the
	 * Euclidean distance between the closest points.
	 */
	public double distance(final Object env) {
		return 0.0;
	}

	// ---------------------------------------------------------------------------------------------------------------

	private Envelope3D() {
		super();
	}

	/**
	 * Computes the intersection of two {@link Envelope}s.
	 *
	 * @param env the envelope to intersect with
	 * @return a new Object representing the intersection of the envelopes (this
	 *         will be the null envelope if either argument is null, or they do not
	 *         intersect
	 */
	public Envelope3D intersection(final Object env) {
		return null;
	}

	/**
	 * Enlarges this <code>Envelope</code> so that it contains the
	 * <code>other</code> Envelope. Has no effect if <code>other</code> is wholly on
	 * or within the envelope.
	 *
	 * @param other the <code>Envelope</code> to expand to include
	 */
	public void expandToInclude(final Object other) {
		return;
	}

	/**
	 * @param other
	 * @return
	 */
	private double getMaxZOf(final Object other) {
		if (other instanceof Envelope3D)
			return ((Envelope3D) other).maxz;
		return 0d;
	}

	/**
	 * @param other
	 * @return
	 */
	private double getMinZOf(final Object other) {
		if (other instanceof Envelope3D)
			return ((Envelope3D) other).minz;
		return 0d;
	}

	/**
	 * Returns a hash value for this envelope. This value need not remain consistent
	 * between different implementations of the same class.
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * Compares the specified object with this envelope for equality.
	 */
	@Override
	public boolean equals(final Object other) {
		return true;
	}

	public boolean isFlat() {
		return minz == maxz;
	}

	public boolean isHorizontal() {
		return minz == maxz;
	}

	public Object toGeometry() {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	public Envelope3D yNegated() {
		return null;
	}

	public Envelope3D rotate(final AxisAngle rotation) {
		return null;
	}

}