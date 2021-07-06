/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.Projection.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;


import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.runtime.IScope;

public class Projection implements IProjection {

	private final ProjectionFactory factory;
	private Object transformer, inverseTransformer;
	Object initialCRS;
	Envelope3D projectedEnv;
	final IProjection referenceProjection;

	Projection(final IProjection world, final ProjectionFactory fact) {
		referenceProjection = world;
		factory = fact;
	}

	Projection(final IScope scope, final IProjection world, final Object crs, final Envelope3D env,
			final ProjectionFactory fact) {
		this.factory = fact;
		this.referenceProjection = world;
		initialCRS = crs;
		if (env != null) {
			// We project the envelope and we use it for initializing the translations
			projectedEnv = transform(env);
			// createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
		}
	}

	@Override
	public void createTransformation(final Object t) {
		return;
	}

	@Override
	public Object transform(final Object g) {
		return null;
	}

	public Object transform(final Object g, final boolean translate) {
		return null;
	}

	Envelope3D transform(final Envelope3D g) {
		return null;
	}

	@Override
	public Object inverseTransform(final Object g) {
		return null;
	}

	Object computeProjection(final IScope scope) {
		return null;
	}

	@Override
	public Object getInitialCRS(final IScope scope) {
		return initialCRS;
	}

	@Override
	public Envelope3D getProjectedEnvelope() {
		return projectedEnv;
	}

	/**
	 * Method getTargetCRS()
	 *
	 * @see msi.gama.metamodel.topology.projection.IProjection#getTargetCRS()
	 */
	@Override
	public Object getTargetCRS(final IScope scope) {
		if (referenceProjection != null) return referenceProjection.getTargetCRS(scope);
		return factory.getTargetCRS(scope);
	}

	/**
	 * Method translate()
	 *
	 */
	@Override
	public void translate(final Object geom) {
		if (referenceProjection != null) { referenceProjection.translate(geom); }
	}

	/**
	 * Method inverseTranslate()
	 *
	 */
	@Override
	public void inverseTranslate(final Object geom) {
		if (referenceProjection != null) { referenceProjection.inverseTranslate(geom); }
	}

	@Override
	public void convertUnit(final Object geom) {
		if (referenceProjection != null) { referenceProjection.convertUnit(geom); }

	}

	@Override
	public void inverseConvertUnit(final Object geom) {
		if (referenceProjection != null) { referenceProjection.inverseConvertUnit(geom); }

	}

}
