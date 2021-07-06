/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.ShapeExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.getPointsOf;
import static msi.gama.common.geometry.GeometryUtils.rotate;
import static msi.gama.common.geometry.GeometryUtils.translate;
import static msi.gama.common.geometry.Scaling3D.of;
import static msi.gaml.operators.Cast.asFloat;
import static msi.gaml.operators.Cast.asGeometry;
import static msi.gaml.types.GamaFileType.createFile;
import static msi.gaml.types.GamaGeometryType.buildArrow;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;

class ShapeExecuter extends DrawExecuter {

	final IExpression endArrow, beginArrow;
	final IShape constantShape;
	final Double constantEnd, constantBegin;
	final boolean hasArrows;
	final GamaPoint center = new GamaPoint();

	ShapeExecuter(final IExpression item, final IExpression beginArrow, final IExpression endArrow)
			throws GamaRuntimeException {
		super(item);
		constantShape = item.isConst() ? asGeometry(null, item.getConstValue()) : null;
		hasArrows = beginArrow != null || endArrow != null;
		if (beginArrow != null) {
			if (beginArrow.isConst()) {
				constantBegin = asFloat(null, beginArrow.getConstValue());
				this.beginArrow = null;
			} else {
				constantBegin = null;
				this.beginArrow = beginArrow;
			}
		} else {
			this.beginArrow = null;
			constantBegin = null;
		}
		if (endArrow != null) {
			if (endArrow.isConst()) {
				constantEnd = asFloat(null, endArrow.getConstValue());
				this.endArrow = null;
			} else {
				constantEnd = null;
				this.endArrow = beginArrow;
			}
		} else {
			this.endArrow = null;
			constantEnd = null;
		}

	}

	@Override
	Rectangle2D executeOn(final IScope scope, final IGraphics gr, final DrawingData data) throws GamaRuntimeException {
		return null;
	}

	DrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final IShape shape) {
		Double depth = data.depth.get();
		if (depth == null) {
			depth = shape.getDepth();
		}
		final DrawingAttributes attributes = new ShapeDrawingAttributes(of(data.size.get()), depth,
				data.rotation.get(), data.getLocation(), data.empty.get(), data.color.get(), /* data.getColors(), */
				data.border.get(), data.texture.get(), data.material.get(), scope.getAgent(),
				shape.getGeometricalType(), data.lineWidth.get(), data.lighting.get());
		return attributes;
	}

	/**
	 * @param scope
	 * @param attributes
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private void addTextures(final IScope scope, final DrawingAttributes attributes) {
		if (attributes.getTextures() == null) { return; }
		attributes.getTextures().replaceAll((s) -> {
			GamaImageFile image = null;
			if (s instanceof GamaImageFile) {
				image = (GamaImageFile) s;
			} else if (s instanceof String) {
				image = (GamaImageFile) createFile(scope, (String) s, null);
			}
			if (image == null || !image.exists(scope)) {
				throw new GamaRuntimeFileException(scope, "Texture file not found: " + s);
			}
			return image;

		});
	}

	/**
	 * @param scope
	 * @param shape
	 * @return
	 */
	private Object addToroidalParts(final IScope scope, final Object shape) {
		return null;
	}

	private final List<Object> tempArrowList = new ArrayList<>();

	private Object addArrows(final IScope scope, final Object g1, final Boolean fill) {
		return null;
	}
}