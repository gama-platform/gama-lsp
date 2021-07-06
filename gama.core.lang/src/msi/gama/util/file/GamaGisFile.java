/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGisFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.util.GISUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;

/**
 * Class GamaGisFile.
 *
 * @author drogoul
 * @since 12 déc. 2013
 *
 */
public abstract class GamaGisFile extends GamaGeometryFile {

	// The code to force reading the GIS data as already projected
	public static final int ALREADY_PROJECTED_CODE = 0;
	protected IProjection gis;
	protected Integer initialCRSCode = null;
	protected String initialCRSCodeStr = null;
	protected boolean with3D = false;

	// Faire les tests sur ALREADY_PROJECTED ET LE PASSER AUSSI A GIS UTILS ???

	/**
	 * Returns the CRS defined with this file (in a ".prj" file or passed by the user)
	 *
	 * @return
	 */
	protected final Object getExistingCRS(final IScope scope) {
		return null;
	}

	/**
	 * @return
	 */
	protected Object getOwnCRS(final IScope scope) {
		return null;
	}

	protected abstract Object getFeatureCollection(final IScope scope);

	protected void readShapes(final IScope scope) {
	return;
	}

	protected void computeProjection(final IScope scope, final Envelope3D env) {
	return;
	}

	protected Object multiPolygonManagement(final Object geom) {
		return null;
	}

	protected static boolean hasNullElements(final Object[] array) {
		for (final Object element : array) {
			if (element == null) return true;
		}
		return false;
	}

	public GamaGisFile(final IScope scope, final String pathName, final Integer code, final boolean withZ) {
		super(scope, pathName);
		initialCRSCode = code;
		with3D = withZ;
	}

	public GamaGisFile(final IScope scope, final String pathName, final Integer code) {
		super(scope, pathName);
		initialCRSCode = code;
	}

	public GamaGisFile(final IScope scope, final String pathName, final String code) {
		super(scope, pathName);
		initialCRSCodeStr = code;
	}

	public GamaGisFile(final IScope scope, final String pathName, final String code, final boolean withZ) {
		super(scope, pathName);
		initialCRSCodeStr = code;
		with3D = withZ;
	}

	public IProjection getGis(final IScope scope) {
		if (gis == null) { fillBuffer(scope); }
		return gis;
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readShapes(scope);
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		gis = null;
		initialCRSCode = null;
		initialCRSCodeStr = null;
	}

}
