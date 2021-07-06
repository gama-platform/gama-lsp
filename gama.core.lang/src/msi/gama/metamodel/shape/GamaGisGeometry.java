/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaGisGeometry.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.shape;



/**
 *
 * The class GamaGisGeometry. A subclass of GamaGeometry that maintains a link with the underlying GIS feature
 * attributes
 *
 * @author drogoul
 * @since 30 nov. 2011
 *
 */
public class GamaGisGeometry extends GamaShape {

	public GamaGisGeometry(final Object g, final Object feature) {
		super(g);
	}

}