/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.IProjection.java, in plugin msi.gama.core,
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

/**
 * Class IProjection.
 * 
 * @author drogoul
 * @since 17 déc. 2013
 * 
 */
public interface IProjection {

	public abstract void createTransformation(final Object t);

	public abstract Object transform(final Object g);

	public abstract Object inverseTransform(final Object g);

	public abstract Object getInitialCRS(IScope scope);

	public abstract Object getTargetCRS(IScope scope);

	public abstract Envelope3D getProjectedEnvelope();

	/**
	 * @param geom
	 */
	public abstract void translate(Object geom);

	public abstract void inverseTranslate(Object geom);
	

	public abstract void convertUnit(Object geom);

	public abstract void inverseConvertUnit(Object geom);

}