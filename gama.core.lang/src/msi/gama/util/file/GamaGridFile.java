/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGridFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import static msi.gama.common.geometry.Envelope3D.of;
import static msi.gama.runtime.GAMA.reportError;
import static msi.gama.runtime.exceptions.GamaRuntimeException.error;
import static msi.gama.runtime.exceptions.GamaRuntimeException.warning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file (
		name = "grid",
		extensions = { "asc", "tif" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.GRID, IConcept.ASC, IConcept.TIF, IConcept.FILE },
		doc = @doc ("Represents .asc or .tif files that contain grid descriptions"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGridFile extends GamaGisFile implements IFieldMatrixProvider {

	class Records {
		double x[];
		double y[];
		final List<double[]> bands = new ArrayList<>();

		public void fill(final int i, final IList<Double> bands2) {
			for (double[] tab : bands) {
				bands2.add(tab[i]);
			}
		}
	}

	Object coverage;
	public int nbBands, numRows, numCols;
	IShape geom;
	Number noData = -9999;
	Object genv;
	Records records;

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// No attributes
		return GamaListFactory.EMPTY_LIST;
	}

	private void createCoverage(final IScope scope) {
	return;
	}

	private void privateCreateCoverage(final IScope scope, final InputStream fis)
			throws IOException {
	return;
	}

	private InputStream fixFileHeader(final IScope scope) {
		final StringBuilder text = new StringBuilder();
		final String NL = System.getProperty("line.separator");

		try (Scanner scanner = new Scanner(getFile(scope))) {
			// final int cpt = 0;
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				if (line.contains("dx")) {
					text.append(line.replace("dx", "cellsize") + NL);
				} else {
					text.append(line + NL);
				}
			}
		} catch (final FileNotFoundException e2) {
			throw error("The format of " + getName(scope) + " is not correct. Error: " + e2.getMessage(), scope);
		}

		text.append(NL);
		// fis = new StringBufferInputStream(text.toString());
		return new StringBufferInputStream(text.toString());
	}

	void read(final IScope scope, final boolean readAll, final boolean createGeometries) {
		return;
	}

	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\");",
					isExecutable = false) })

	public GamaGridFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file, but without converting it into shapes. Only a matrix of float values is created",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\", false);",
					isExecutable = false) })

	public GamaGridFile(final IScope scope, final String pathName, final boolean asMatrix) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file specifying the coordinates system code, as an int (epsg code)",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\", 32648);",
					isExecutable = false) })
	public GamaGridFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file specifying the coordinates system code (epg,...,), as a string ",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\",\"EPSG:32648\");",
					isExecutable = false) })
	public GamaGridFile(final IScope scope, final String pathName, final String code) {
		super(scope, pathName, code);
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		if (gis == null) { createCoverage(scope); }
		return gis.getProjectedEnvelope();
		// OLD : see what it changes to not do it
		// fillBuffer(scope);
		// return gis.getProjectedEnvelope();
	}

	@Override
	protected void fillBuffer(final IScope scope) {
		if (getBuffer() != null) return;
		createCoverage(scope);
		read(scope, true, true);
	}

	public int getNbRows(final IScope scope) {
		createCoverage(scope);
		return numRows;
	}

	public boolean isTiff(final IScope scope) {
		return getExtension(scope).equals("tif");
	}

	@Override
	public IShape getGeometry(final IScope scope) {
		createCoverage(scope);
		read(scope, false, false);
		return geom;
	}

	@Override
	protected Object getOwnCRS(final IScope scope) {
		return null;
	}

	@Override
	public void invalidateContents() {
		return;
	}

	public Double valueOf(final IScope scope, final ILocation loc) {
		return valueOf(scope, loc.getX(), loc.getY());
	}

	public Double valueOf(final IScope scope, final double x, final double y) {
		return null;
	}

	@Override
	public int length(final IScope scope) {
		createCoverage(scope);
		return numRows * numCols;
	}

	@Override
	protected Object getFeatureCollection(final IScope scope) {
		return null;
	}

	@Override
	public double getNoData(final IScope scope) {
		return noData.doubleValue();
	}

	@Override
	public int getRows(final IScope scope) {
		createCoverage(scope);
		return numRows;
	}

	@Override
	public int getCols(final IScope scope) {
		createCoverage(scope);
		return numCols;
	}

	@Override
	public int getBandsNumber(final IScope scope) {
		createCoverage(scope);
		return nbBands;
	}

	@Override
	public double[] getBand(final IScope scope, final int index) {
		createCoverage(scope);
		read(scope, true, false);
		return records.bands.get(index);
	}

}
