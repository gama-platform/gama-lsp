/*******************************************************************************************************
 *
 * msi.gaml.statements.SaveStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import static msi.gama.common.util.FileUtils.constructAbsoluteFilePath;
import static msi.gama.util.graph.writer.GraphExporters.getAvailableWriters;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ITyped;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.SimpleScalingProjection;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.writer.GraphExporters;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.MapExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Comparison;
import msi.gaml.operators.Strings;
import msi.gaml.skills.GridSkill.IGridAgent;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.SaveStatement.SaveValidator;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaKmlExport;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@symbol(name = IKeyword.SAVE, kind = ISymbolKind.SINGLE_STATEMENT, concept = { IConcept.FILE,
		IConcept.SAVE_FILE }, with_sequence = true, // necessary to allow declaring the attributes facet as remote
													// itself
		// with_args = true,
		remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
@facets(value = { @facet(name = IKeyword.TYPE, type = IType.ID, optional = true, values = { "shp", "text", "csv", "asc",
		"geotiff", "image", "kml", "kmz", "json", "dimacs", "dot", "gexf", "graphml", "gml",
		"graph6" }, doc = @doc("an expression that evaluates to an string, the type of the output file (it can be only \"shp\", \"asc\", \"geotiff\", \"image\", \"text\" or \"csv\") ")),
		@facet(name = IKeyword.DATA, type = IType.NONE, optional = true, doc = @doc("the data that will be saved to the file")),
		@facet(name = IKeyword.REWRITE, type = IType.BOOL, optional = true, doc = @doc("a boolean expression specifying whether to erase the file if it exists or append data at the end of it. Only applicable to \"text\" or \"csv\" files. Default is true")),
		@facet(name = IKeyword.HEADER, type = IType.BOOL, optional = true, doc = @doc("an expression that evaluates to a boolean, specifying whether the save will write a header if the file does not exist")),
		@facet(name = IKeyword.TO, type = IType.STRING, optional = true, doc = @doc("an expression that evaluates to an string, the path to the file, or directly to a file")),
		@facet(name = "crs", type = IType.NONE, optional = true, doc = @doc("the name of the projection, e.g. crs:\"EPSG:4326\" or its EPSG id, e.g. crs:4326. Here a list of the CRS codes (and EPSG id): http://spatialreference.org")),
		@facet(name = IKeyword.ATTRIBUTES, type = { IType.MAP,
				IType.LIST }, remote_context = true, optional = true, doc = @doc(value = "Allows to specify the attributes of a shape file or GeoJson file where agents are saved. Can be expressed as a list of string or as a literal map. When expressed as a list, each value should represent the name of an attribute of the shape or agent. The keys of the map are the names of the attributes that will be present in the file, the values are whatever expressions neeeded to define their value. ")),
		@facet(name = IKeyword.WITH, type = {
				IType.MAP }, optional = true, doc = @doc(deprecated = "Please use 'attributes:' instead", value = "Allows to define the attributes of a shape file. Keys of the map are the attributes of agents to save, values are the names of attributes in the shape file")) }, omissible = IKeyword.DATA)
@doc(value = "Allows to save data in a file. The type of file can be \"shp\", \"asc\", \"geotiff\", \"text\" or \"csv\".", usages = {
		@usage(value = "Its simple syntax is:", examples = {
				@example(value = "save data to: output_file type: a_type_file;", isExecutable = false) }),
		@usage(value = "To save data in a text file:", examples = {
				@example(value = "save (string(cycle) + \"->\"  + name + \":\" + location) to: \"save_data.txt\" type: \"text\";") }),
		@usage(value = "To save the values of some attributes of the current agent in csv file:", examples = {
				@example(value = "save [name, location, host] to: \"save_data.csv\" type: \"csv\";") }),
		@usage(value = "To save the values of all attributes of all the agents of a species into a csv (with optional attributes):", examples = {
				@example(value = "save species_of(self) to: \"save_csvfile.csv\" type: \"csv\" header: false;") }),
		@usage(value = "To save the geometries of all the agents of a species into a shapefile (with optional attributes):", examples = {
				@example(value = "save species_of(self) to: \"save_shapefile.shp\" type: \"shp\" attributes: ['nameAgent'::name, 'locationAgent'::location] crs: \"EPSG:4326\";") }),
		@usage(value = "To save the grid_value attributes of all the cells of a grid into an ESRI ASCII Raster file:", examples = {
				@example(value = "save grid to: \"save_grid.asc\" type: \"asc\";") }),
		@usage(value = "To save the grid_value attributes of all the cells of a grid into geotiff:", examples = {
				@example(value = "save grid to: \"save_grid.tif\" type: \"geotiff\";") }),
		@usage(value = "To save the grid_value attributes of all the cells of a grid into png (with a worldfile):", examples = {
				@example(value = "save grid to: \"save_grid.png\" type: \"image\";") }),
		@usage(value = "The save statement can be use in an init block, a reflex, an action or in a user command. Do not use it in experiments.") })
@validator(SaveValidator.class)
@SuppressWarnings({ "rawtypes" })
public class SaveStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	public static class SaveValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {

			final StatementDescription desc = description;
			final Facets with = desc.getPassedArgs();
			final IExpression att = desc.getFacetExpr(ATTRIBUTES);
			final boolean isMap = att instanceof MapExpression;
			if (att != null) {
				if (!isMap && !att.getGamlType().isTranslatableInto(Types.LIST.of(Types.STRING))) {
					desc.error("attributes must be expressed as a map<string, unknown> or as a list<string>",
							IGamlIssue.WRONG_TYPE, ATTRIBUTES);
					return;
				}
				if (isMap) {
					final MapExpression map = (MapExpression) att;
					if (map.getGamlType().getKeyType() != Types.STRING) {
						desc.error(
								"The type of the keys of the attributes map must be string. These will be used for naming the attributes in the file",
								IGamlIssue.WRONG_TYPE, ATTRIBUTES);
						return;
					}
				}

				if (with.exists()) {
					desc.warning(
							"'with' and 'attributes' are mutually exclusive. Only the first one will be considered",
							IGamlIssue.CONFLICTING_FACETS, ATTRIBUTES, WITH);
				}
				final IExpression type = desc.getFacetExpr(TYPE);
				if (type == null || !(type.literalValue().equals("shp") || type.literalValue().equals("json"))) {
					desc.warning("Attributes can only be defined for shape or json files", IGamlIssue.WRONG_TYPE,
							ATTRIBUTES);
				}

			}

			final IExpression data = desc.getFacetExpr(DATA);
			if (data == null) {
				return;
			}
			final IType<?> t = data.getGamlType().getContentType();
			final SpeciesDescription species = t.getSpecies();

			if (att == null && !with.exists()) {
				return;
			}

			if (species == null) {
				if (with.exists() || isMap) {
					desc.error("Attributes of geometries can only be specified with a list of attribute names",
							IGamlIssue.UNKNOWN_FACET, att == null ? WITH : ATTRIBUTES);
				}
				// Error deactivated for fixing #2982.
				// desc.error("Attributes can only be saved for agents",
				// IGamlIssue.UNKNOWN_FACET,
				// att == null ? WITH : ATTRIBUTES);
			} else {
				with.forEachFacet((name, exp) -> {
					if (!species.hasAttribute(name)) {
						desc.error("Attribute " + name + " is not defined for the agents of " + data.serialize(false),
								IGamlIssue.UNKNOWN_VAR, WITH);
						return false;
					}
					return true;
				});
			}
		}

	}

	private Arguments withFacet;
	private final IExpression attributesFacet;
	private final IExpression crsCode, item, file, rewriteExpr, header;

	public SaveStatement(final IDescription desc) {
		super(desc);
		crsCode = desc.getFacetExpr("crs");
		item = desc.getFacetExpr(IKeyword.DATA);
		file = getFacet(IKeyword.TO);
		rewriteExpr = getFacet(IKeyword.REWRITE);
		header = getFacet(IKeyword.HEADER);
		attributesFacet = getFacet(IKeyword.ATTRIBUTES);
	}

	private boolean shouldOverwrite(final IScope scope) {
		if (rewriteExpr == null) {
			return true;
		}
		return Cast.asBool(scope, rewriteExpr.value(scope));
	}

	// TODO rewrite this with the GamaFile framework

	@SuppressWarnings("unchecked")
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (item == null) {
			return null;
		}
		// First case: we have a file as item;
		if (file == null) {
			if (Types.FILE.isAssignableFrom(item.getGamlType())) {
				final IGamaFile file = (IGamaFile) item.value(scope);
				if (file != null) {
					// Passes directly the facets of the statement, like crs, etc.
					file.save(scope, description.getFacets());
				}
				return file;
			} else {
				return null;
			}
		}
		final String typeExp = getLiteral(IKeyword.TYPE);
		// Second case: a filename is indicated but not the type. In that case,
		// we try to build a new GamaFile from it and save it
		if (file != null && typeExp == null) {
			final String name = Cast.asString(scope, file.value(scope));
			final Object contents = item.value(scope);
			if (contents instanceof IModifiableContainer) {
				final IGamaFile f = GamaFileType.createFile(scope, name, (IModifiableContainer) contents);
				f.save(scope, description.getFacets());
				return f;
			}

		}

		// These statements will need to be completely rethought because of the
		// possibility to now use the GamaFile infrastructure for this.
		// For instance, TYPE is not needed anymore (the name of the file / its
		// inner type will be enough), like in save json_file("ddd.json",
		// my_map); which we can probably allow to be written save my_map to:
		// json_file("ddd.json"); see #1362

		try {
			final String path = constructAbsoluteFilePath(scope, Cast.asString(scope, file.value(scope)), false);
			if (path == null || path.equals("")) {
				return null;
			}
			final File fileToSave = new File(path);
			createParents(fileToSave);
			boolean exists = fileToSave.exists();
			final String type = (typeExp != null ? typeExp : "text").trim().toLowerCase();
			//
			switch (type) {
			case "shp":
			case "json":
				Object agents = item.value(scope);
				if (agents instanceof ISpecies) {
					agents = scope.getAgent().getPopulationFor((ISpecies) agents);
				} else if (agents instanceof IShape) {
					// see Issue #2857
					agents = GamaListFactory.wrap(item.getGamlType(), agents);
				} else if (!(agents instanceof IList)) {
					return null;
				}
				saveShape((IList<? extends IShape>) agents, fileToSave, scope, type.equals("json"));
				break;
			case "text":
			case "csv":
				final boolean rewrite = shouldOverwrite(scope);
				if (rewrite) {
					if (exists) {
						fileToSave.delete();
						exists = false;
					}
				}
				fileToSave.createNewFile();
				final boolean addHeader = !exists && (header == null || Cast.asBool(scope, header.value(scope)));
				saveText(type, fileToSave, addHeader, scope);
				break;
			case "asc":
				final ISpecies species1 = Cast.asSpecies(scope, item.value(scope));
				if (species1 == null || !species1.isGrid()) {
					return null;
				}
				saveAsc(species1, fileToSave, scope);
				break;
			case "geotiff":
			case "image":
				final ISpecies species2 = Cast.asSpecies(scope, item.value(scope));
				if (species2 == null || !species2.isGrid()) {
					return null;
				}
				saveRasterImage(species2, path, scope, type.equals("geotiff"));
				break;
			case "kml":
			case "kmz":
				final Object kml = item.value(scope);
				if (!(kml instanceof GamaKmlExport)) {
					return null;
				}
				if (type.equals("kml")) {
					((GamaKmlExport) kml).saveAsKml(scope, path);
				} else {
					((GamaKmlExport) kml).saveAsKmz(scope, path);
				}
				break;
			default:
				if (getAvailableWriters().contains(type)) {
					final IGraph g = Cast.asGraph(scope, item);
					if (g == null) {
						return null;
					}
					this.saveGraph(g, fileToSave, type, scope);
				} else {
					throw GamaRuntimeFileException.error("Format is not recognized ('" + type + "')", scope);
				}
			}
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final IOException e) {
			throw GamaRuntimeFileException.create(e, scope);
		}

		return Cast.asString(scope, file.value(scope));
	}

	private static void createParents(final File outputFile) {
		final File parent = outputFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

	}

	public void saveAsc(final ISpecies species, final File f, final IScope scope) {
		return;
	}

	public void saveRasterImage(final ISpecies species, final String p, final IScope scope, final boolean toGeotiff) {
		return;
	}

	// Inspired by the code of public GridCoverage2D create(final CharSequence name,
	// final float[][] matrix, final
	// Envelope envelope)
	// from org.geotools.coverage.grid.GridCoverageFactory
	public static Object createCoverageByteFromFloat(final CharSequence name, final float[][] matrix,
			final Object envelope) {
		return null;
	}

	public static String getGeometryType(final List<? extends IShape> agents) {
		return null;
	}

	public void saveGraph(final IGraph g, final File f, final String type, final IScope scope) {
		return;
	}

	public void saveShape(final IList<? extends IShape> agents, final File f, final IScope scope, final boolean geoJson)
			throws GamaRuntimeException {
		return;
	}

	public IProjection defineProjection(final IScope scope, final File f) {
		return null;
	}

	public void saveText(final String type, final File fileTxt, final boolean header, final IScope scope)
			throws GamaRuntimeException {
		try (FileWriter fw = new FileWriter(fileTxt, true)) {
			if (type.equals("text")) {
				fw.write(Cast.asString(scope, item.value(scope)) + Strings.LN);
			} else if (type.equals("csv")) {
				final IType itemType = item.getGamlType();
				final SpeciesDescription sd;
				if (itemType.isAgentType()) {
					sd = itemType.getSpecies();
				} else if (itemType.getContentType().isAgentType()) {
					sd = itemType.getContentType().getSpecies();
				} else {
					sd = null;
				}
				final Object value = item.value(scope);
				final IList values = itemType.isContainer() ? Cast.asList(scope, value)
						: GamaListFactory.create(scope, itemType, value);
				if (values.isEmpty()) {
					return;
				}
				if (sd != null) {
					final Collection<String> attributeNames = sd.getAttributeNames();
					attributeNames.removeAll(NON_SAVEABLE_ATTRIBUTE_NAMES);
					if (header) {
						fw.write("cycle;name;location.x;location.y;location.z");
						for (final String v : attributeNames) {
							fw.write(";" + v);
						}
						fw.write(Strings.LN);
					}
					for (final Object obj : values) {
						if (obj instanceof IAgent) {
							final IAgent ag = Cast.asAgent(scope, obj);
							fw.write(scope.getClock().getCycle() + ";" + ag.getName().replace(';', ',') + ";"
									+ ag.getLocation().getX() + ";" + ag.getLocation().getY() + ";"
									+ ag.getLocation().getZ());
							for (final String v : attributeNames) {
								String val = Cast.toGaml(ag.getDirectVarValue(scope, v)).replace(';', ',');
								if (val.startsWith("'") && val.endsWith("'")
										|| val.startsWith("\"") && val.endsWith("\"")) {
									val = val.substring(1, val.length() - 1);
								}
								fw.write(";" + val);
							}
							fw.write(Strings.LN);
						}

					}
				} else {
					if (header) {
						fw.write(item.serialize(true).replace("]", "").replace("[", ""));
						fw.write(Strings.LN);
					}
					if (itemType.id() == IType.MATRIX) {
						final String[] tmpValue = value.toString().replace("[", "").replace("]", "").split(",");
						for (int i = 0; i < tmpValue.length; i++) {
							if (i > 0) {
								fw.write(',');
							}
							fw.write(toCleanString(tmpValue[i]));
						}
						fw.write(Strings.LN);
					} else {
						final int size = values.size();
						for (int i = 0; i < size; i++) {
							if (i > 0) {
								fw.write(',');
							}
							fw.write(toCleanString(values.get(i)));
						}
						fw.write(Strings.LN);
					}
				}

			}

		} catch (final IOException io) {
			// Could catch the exception when multiple threads try to access the file
			// See Issue #2796. But it'd be a bad idea.
			// DEBUG.OUT("Ignoring IOException");
			throw GamaRuntimeFileException.create(io, scope);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}

	}

	public String toCleanString(final Object o) {
		String val = Cast.toGaml(o).replace(';', ',');
		if (val.startsWith("'") && val.endsWith("'") || val.startsWith("\"") && val.endsWith("\"")) {
			val = val.substring(1, val.length() - 1);
		}

		if (o instanceof String) {
			val = val.replace("\\'", "'");
			val = val.replace("\\\"", "\"");

		}
		return val;
	}

	public String type(final ITyped var) {
		switch (var.getGamlType().id()) {
		case IType.BOOL:
			return "Boolean";
		case IType.INT:
			return "Integer";
		case IType.FLOAT:
			return "Double";
		default:
			return "String";
		}
	}

	private static final Set<String> NON_SAVEABLE_ATTRIBUTE_NAMES = new HashSet<>(Arrays.asList(IKeyword.PEERS,
			IKeyword.LOCATION, IKeyword.HOST, IKeyword.AGENTS, IKeyword.MEMBERS, IKeyword.SHAPE));

	private void computeInitsFromWithFacet(final IScope scope, final Arguments withFacet,
			final Map<String, IExpression> values, final SpeciesDescription species) throws GamaRuntimeException {
		if (species == null) {
			return;
		}
		if (withFacet.isEmpty()) {
			for (final String var : species.getAttributeNames()) {
				if (!NON_SAVEABLE_ATTRIBUTE_NAMES.contains(var)) {
					values.put(var, species.getVarExpr(var, false));
				}
			}
		} else {
			withFacet.forEachFacet((key, value) -> {
				values.put(value.getExpression().literalValue(), species.getVarExpr(key, false));
				return true;
			});
		}
	}

	private void computeInitsFromAttributesFacet(final IScope scope, final Map<String, IExpression> values,
			final SpeciesDescription species) throws GamaRuntimeException {
		if (attributesFacet instanceof MapExpression) {
			final Map<IExpression, IExpression> map = ((MapExpression) attributesFacet).getElements();
			map.forEach((key, value) -> {
				final String name = Cast.asString(scope, key.value(scope));
				values.put(name, value);
			});
		} else {
			final List<String> names = GamaListFactory.create(scope, Types.STRING,
					Cast.asList(scope, attributesFacet.value(scope)));
			if (species != null) {
				names.forEach(n -> values.put(n,
						species.hasAttribute(n) ? species.getVarExpr(n, false) : IExpressionFactory.NIL_EXPR));
			} else {
				// see #2982
				names.forEach(n -> values.put(n, new ConstantExpression(n)));
			}
		}
	}

	private static Object fixesPolygonCWS(final Object g) {
		return null;
	}

	public static boolean buildFeature(final IScope scope, final Object ff, final IShape ag, final IProjection gis,
			final Collection<IExpression> attributeValues) {
		return true;
	}

	// AD 2/1/16 Replace IAgent by IShape so as to be able to save geometries
	public static void saveGeoJSonFile(final IScope scope, final File f, final List<? extends IShape> agents,
			/* final String featureTypeName, */final String specs, final Map<String, IExpression> attributes,
			final IProjection gis) throws IOException, GamaRuntimeException {
		return;
	}

	// AD 2/1/16 Replace IAgent by IShape so as to be able to save geometries
	public static void saveShapeFile(final IScope scope, final File f, final List<? extends IShape> agents,
			/* final String featureTypeName, */final String specs, final Map<String, IExpression> attributes,
			final IProjection gis) throws IOException, GamaRuntimeException {
		return;
	}

	private static Object geometryCollectionManagement(final Object gg) {
		return null;
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		withFacet = args;
	}

	@Override
	public void setRuntimeArgs(final IScope scope, final Arguments args) {
		// TODO Auto-generated method stub
	}
}
