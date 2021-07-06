package msi.gama.metamodel.topology.projection;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

public class SimpleScalingProjection implements IProjection {

	public Object scaling, inverseScaling;

	@Override
	public void createTransformation(Object t) {

	}

	public SimpleScalingProjection(Double scale) {
		if (scale != null) {
			createScalingTransformations(scale);
		}

	}

	@Override
	public Object transform(Object geom) {
		if (scaling != null) {
			return null;
		}
		return geom;
	}

	@Override
	public Object inverseTransform(Object geom) {
		return null;
	}

	public void createScalingTransformations(final Double scale) {
		return;
	}

	@Override
	public Object getInitialCRS(IScope scope) {
		return null;
	}

	@Override
	public Object getTargetCRS(IScope scope) {
		return null;
	}

	@Override
	public Envelope3D getProjectedEnvelope() {
		return null;
	}

	@Override
	public void translate(Object geom) {

	}

	@Override
	public void inverseTranslate(Object geom) {

	}

	@Override
	public void convertUnit(Object geom) {
	}

	@Override
	public void inverseConvertUnit(Object geom) {

	}

}
