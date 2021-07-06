/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaShapeFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.util.GISUtils;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file(name = "shape", extensions = {
		"shp" }, buffer_type = IType.LIST, buffer_content = IType.GEOMETRY, buffer_index = IType.INT, concept = {
				IConcept.SHAPEFILE,
				IConcept.FILE }, doc = @doc("Represents a shape file as defined by the ESRI standard. See https://en.wikipedia.org/wiki/Shapefile for more information."))
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaShapeFile extends GamaGisFile {

	static {
		DEBUG.ON();
	}

	// FileDataStore store;

	public static class ShapeInfo extends GamaFileMetaData {

		final int itemNumber;
		final Object crs;
		final double width;
		final double height;
		final Map<String, String> attributes = new LinkedHashMap();

		public ShapeInfo(final IScope scope, final URL url, final long modificationStamp) {
			super(modificationStamp);
			this.crs = new Object();
			this.width = 0;
			this.height = 0;
			this.itemNumber = 0;
		}

		public Object getCRS() {
			return null;
		}

		/**
		 * Method getSuffix()
		 *
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			final StringBuilder sb = new StringBuilder();
			appendSuffix(sb);
			return sb.toString();
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			return;
		}

		@Override
		public String getDocumentation() {
			return null;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		@Override
		public String toPropertyString() {
			return null;
		}
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	@doc(value = "This file constructor allows to read a shapefile (.shp) file", examples = {
			@example(value = "file f <- shape_file(\"file.shp\");", isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	@doc(value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code, as an int (epsg code)", examples = {
			@example(value = "file f <- shape_file(\"file.shp\", \"32648\");", isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	@doc(value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code (epg,...,), as a string", examples = {
			@example(value = "file f <- shape_file(\"file.shp\", \"EPSG:32648\");", isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	@doc(value = "This file constructor allows to read a shapefile (.shp) file and take a potential z value (not taken in account by default)", examples = {
			@example(value = "file f <- shape_file(\"file.shp\", true);", isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final boolean with3D) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null, with3D);
	}

	@doc(value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code, as an int (epsg code) and take a potential z value (not taken in account by default)", examples = {
			@example(value = "file f <- shape_file(\"file.shp\", \"32648\", true);", isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final Integer code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	@doc(value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code (epg,...,), as a string and take a potential z value (not taken in account by default)", examples = {
			@example(value = "file f <- shape_file(\"file.shp\", \"EPSG:32648\",true);", isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final String code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		ShapeInfo s;
		final IFileMetaDataProvider p = scope.getGui().getMetaDataProvider();
		if (p != null) {
			s = (ShapeInfo) p.getMetaData(getFile(scope), false, true);
		} else {
			try {
				s = new ShapeInfo(scope, getFile(scope).toURI().toURL(), 0);
			} catch (final MalformedURLException e) {
				return GamaListFactory.EMPTY_LIST;
			}
		}
		return GamaListFactory.wrap(Types.STRING, s.attributes.keySet());
	}

	static Object getDataStore(final URL url) {
		return null;
	}

	@Override
	protected final void readShapes(final IScope scope) {
		return;
	}

	@Override
	protected Object getFeatureCollection(final IScope scope) {
		return null;
	}

	@Override
	public int length(final IScope scope) {
		return 0;
	}

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		// if (store != null) { store.dispose(); }
		// store = null;
	}

}
