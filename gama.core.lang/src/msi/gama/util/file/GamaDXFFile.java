/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaDXFFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file(name = "dxf", extensions = {
		"dxf" }, buffer_type = IType.LIST, buffer_content = IType.GEOMETRY, buffer_index = IType.INT, concept = {
				IConcept.DXF,
				IConcept.FILE }, doc = @doc("DXF files are 2D geometrical files. The internal representation is a list of geometries"))
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaDXFFile extends GamaGeometryFile {

	GamaPoint size;
	Double unit;
	double x_t;
	double y_t;
	protected static final double QUARTER_CIRCLE_ANGLE = Math.tan(0.39269908169872414D);

	@doc(value = "This file constructor allows to read a dxf (.dxf) file", examples = {
			@example(value = "file f <- dxf_file(\"file.dxf\");", isExecutable = false) })
	public GamaDXFFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@doc(value = "This file constructor allows to read a dxf (.dxf) file and specify the unit (meter by default)", examples = {
			@example(value = "file f <- dxf_file(\"file.dxf\",#m);", isExecutable = false) })
	public GamaDXFFile(final IScope scope, final String pathName, final Double unit) throws GamaRuntimeException {
		super(scope, pathName);
		if (unit <= 0) {
			GamaRuntimeException.error("the unity given has to be higher than 0", scope);
		}
		this.unit = unit;
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.EMPTY_LIST;
	}

	public IShape createPolyline(final IScope scope, final IList pts) {
		if (pts.isEmpty()) {
			return null;
		}
		final IShape shape = GamaGeometryType.buildPolyline(pts);
		if (shape != null) {
			if (size != null) {
				return Spatial.Transformations.scaled_to(scope, shape, size);
			}
			return shape;
		}
		return null;
	}

	public IShape createPolygon(final IScope scope, final IList pts) {
		if (pts.isEmpty()) {
			return null;
		}
		final IShape shape = GamaGeometryType.buildPolygon(pts);
		if (shape != null) {
			if (size != null) {
				return Spatial.Transformations.scaled_to(scope, shape, size);
			}
			return shape;
		}
		return null;
	}

	public IShape createCircle(final IScope scope, final GamaPoint location, final double radius) {
		IShape shape = GamaGeometryType.buildCircle(radius, location).getExteriorRing(scope);
		if (shape != null) {
			if (size != null) {
				return Spatial.Transformations.scaled_to(scope, shape, size);
			}
			return shape;
		}
		return null;
	}

	public GamaPoint toGamaPoint(Object v) {
		return null;
	}

	protected void addToLists(IScope scope, Object pline, Object start, Object end, IList list) {
		return;
	}

	public IList<ILocation> getPoints(IScope scope, final Object obj) {
		return null;
	}

	public IShape manageObj(final IScope scope, final Object obj) {
		return null;
	}

	public IShape defineGeom(final IScope scope, final Object obj) {
		return null;
	}

	protected void fillBuffer(final IScope scope, final Object doc) {
		return;
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
	return;
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}
}
