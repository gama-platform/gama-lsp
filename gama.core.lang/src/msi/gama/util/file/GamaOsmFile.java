/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaOsmFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

@file (
		name = "osm",
		extensions = { "osm", "pbf", "bz2", "gz" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.OSM, IConcept.FILE },
		doc = @doc ("Represents files that contain OSM GIS information. The internal representation is a list of geometries. See https://en.wikipedia.org/wiki/OpenStreetMap for more information"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaOsmFile extends GamaGisFile {

	final Object env = null;

	public static class OSMInfo extends GamaFileMetaData {

		int itemNumber;
		Object crs;
		final double width;
		final double height;
		final Map<String, String> attributes = new LinkedHashMap();

		public OSMInfo(final URL url, final long modificationStamp) {
			this.itemNumber = 0;
			this.height = 0;
			this.width = 0;
			this.crs = null;
		}

		public Object getCRS() {
			return null;
		}

		public OSMInfo(final String propertiesString) {
			this.itemNumber = 0;
			this.height = 0;
			this.width = 0;
			this.crs = null;
		}

		/**
		 * Method getSuffix()
		 *
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			return hasFailed ? "error: decompress the file to a .osm file"
					: "" + itemNumber + " objects | " + Math.round(width) + "m x " + Math.round(height) + "m";
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			if (hasFailed) {
				sb.append("error: decompress the file to a .osm file");
				return;
			}
			sb.append(itemNumber).append(" object");
			if (itemNumber > 1) { sb.append("s"); }
			sb.append(SUFFIX_DEL);
			sb.append(Math.round(width)).append("m x ");
			sb.append(Math.round(height)).append("m");
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

	IMap<String, IList> filteringOptions;
	Map<String, String> attributes = new HashMap<>();

	final IMap<String, List<IShape>> layers = GamaMapFactory.create(Types.STRING, Types.LIST);
	final static List<String> featureTypes = Arrays.asList("aerialway", "aeroway", "amenity", "barrier", "boundary",
			"building", "craft", "emergency", "geological", "highway", "historic", "landuse", "leisure", "man_made",
			"military", "natural", "office", "place", "power", "public_transport", "railway", "route", "shop", "sport",
			"tourism", "waterway");

	int nbObjects;

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	@doc (
			value = "This file constructor allows to read a osm (.osm, .pbf, .bz2, .gz) file (using WGS84 coordinate system for the data)",
			examples = { @example (
					value = "file f <- osm_file(\"file\");",
					isExecutable = false) })
	public GamaOsmFile(final IScope scope, final String pathName) {
		super(scope, pathName, (Integer) null);
	}

	@doc (
			value = "This file constructor allows to read an osm (.osm, .pbf, .bz2, .gz) file (using WGS84 coordinate system for the data)"
					+ "The map is used to filter the objects in the file according their attributes: for each key (string) of the map, only the objects that have a value for the  attribute "
					+ "contained in the value set are kept."
					+ " For an exhaustive list of the attibute of OSM data, see: http://wiki.openstreetmap.org/wiki/Map_Features",

			examples = { @example (
					value = "file f <- osm_file(\"file\", map([\"highway\"::[\"primary\", \"secondary\"], \"building\"::[\"yes\"], \"amenity\"::[]]));",
					equals = "f will contain all the objects of file that have the attibute 'highway' with the value 'primary' or 'secondary', and the objects that have the attribute 'building' with the value 'yes', "
							+ "and all the objects that have the attribute 'aminity' (whatever the value).",
					isExecutable = false) })

	public GamaOsmFile(final IScope scope, final String pathName, final IMap<String, IList> filteringOptions) {
		super(scope, pathName, (Integer) null);
		this.filteringOptions = filteringOptions;
	}

	@Override
	protected String fetchFromURL(final IScope scope) {
		String pathName = super.fetchFromURL(scope);
		if (pathName.endsWith(".osm.xml")) { pathName = pathName.replace(".xml", ""); }
		return pathName;
	}

	public void getFeatureIterator(final IScope scope, final boolean returnIt) {
	return;
	}

	private void addAttribute(final Map<String, String> atts, final String nameAt, final Object val) {
		final String type = atts.get(nameAt);
		if (type != null && type.equals("string")) return;
		String newType = "int";
		try {
			Integer.parseInt(val.toString());
		} catch (final Exception e) {
			try {
				Double.parseDouble(val.toString());
			} catch (final Exception e2) {
				newType = "string";
			}
		}

		if (type == null || newType.equals("string")) { atts.put(nameAt, newType); }
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		getFeatureIterator(scope, true);
	}

	public IList<IShape> buildGeometries(final IScope scope, final List<Object> nodes, final List<Object> ways,
			final List<Object> relations, final Set<Long> intersectionNodes, final Map<Long, GamaShape> nodesPt) {
		return null;
	}

	public List<IShape> createSplitRoad(final List<Object> wayNodes, final Map<String, Object> values,
			final Set<Long> intersectionNodes, final Map<Long, GamaShape> nodesPt) {
		return null;
	}

	private IShape createRoad(final List<IShape> points, final Map<String, Object> values) {
		return null;
	}

	void registerHighway(final Object way, final Set<Long> usedNodes, final Set<Long> intersectionNodes) {
		return;
	}

	private void readFile(final IScope scope, final Object sink, final File osmFile) {
		return;
	}

	private void readXML(final IScope scope, final Object sink) throws GamaRuntimeException {
		return;
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		if (gis == null) { getFeatureIterator(scope, false); }
		if (gis == null) return Envelope3D.of(env);
		return gis.getProjectedEnvelope();

	}

	/**
	 * Method getExistingCRS()
	 *
	 * @see msi.gama.util.file.GamaGisFile#getExistingCRS()
	 */
	@Override
	protected Object getOwnCRS(final IScope scope) {
		// Is it always true ?
		return true;
	}

	public Map<String, String> getOSMAttributes(final IScope scope) {
		if (attributes == null) {
			attributes = new HashMap<>();
			getFeatureIterator(scope, true);
		}
		return attributes;
	}

	public Map<String, List<IShape>> getLayers() {
		return layers;
	}

	public List<String> getFeatureTypes() {
		return featureTypes;
	}

	@Override
	protected Object getFeatureCollection(final IScope scope) {
		return null;
	}

}
