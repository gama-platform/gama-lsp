/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.WorldProjection.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

public class WorldProjection extends Projection {

	public Object gisToAbsoluteTranslation, absoluteToGisTranslation;

	public Object otherUnitToMeter, meterToOtherUnit;

	public WorldProjection(final IScope scope, final Object crs, final Envelope3D env, final ProjectionFactory fact) {
		super(scope, null, crs, env, fact);
		// referenceProjection = this;
		/*
		 * Remove the translation: this one is computed only when the world agent
		 * geometry is modified. if ( env != null ) {
		 * createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(),
		 * projectedEnv.getMinY()); }
		 */
	}

	@Override
	public void translate(final Object geom) {
		return;
	}

	@Override
	public void inverseTranslate(final Object geom) {
		return;
	}

	@Override
	public void convertUnit(Object geom) {
		return;
	}

	@Override
	public void inverseConvertUnit(Object geom) {
		return;
	}

	public void updateTranslations(final Envelope3D env) {
		return;
	}

	public void updateUnit(final Object unitConverter) {
		return;
	}

	public void createTranslations(final double minX, final double height, final double minY) {
		return;
	}

	public void createUnitTransformations(final Object unitConverter) {
		return;
	}

}