/*******************************************************************************************************
 *
 * msi.gaml.types.GamaKmlExport.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gaml.operators.Spatial;

public class GamaKmlExport {
	private final Object kml;
	private final Object doc;
	private KmlFolder defolder; // Default folder in case we need one.
	private final HashMap<String, KmlFolder> folders;

	public GamaKmlExport() {
		kml = null;
		doc = null;
		folders = new HashMap<>();
	}

	public KmlFolder addFolder(final String label, final GamaDate beginDate, final GamaDate endDate) {
		return null;
	}

	public void add3DModel(final IScope scope, final GamaPoint loc, final double orientation, final double scale,
			final GamaDate beginDate, final GamaDate endDate, final String daefile) {
		getDefaultFolder().add3DModel(scope, loc, orientation, scale, dateToKml(beginDate), dateToKml(endDate),
				daefile);
	}

	public void addGeometry(final IScope scope, final String label, final GamaDate beginDate, final GamaDate endDate,
			final IShape geom, final String styleName, final double height) {
		getDefaultFolder().addGeometry(scope, label, dateToKml(beginDate), dateToKml(endDate), geom, styleName, height);
	}

	/**
	 * Defines a new style to be used with addStyledRecord
	 *
	 * @param name      Style name. Must be unique.
	 * @param lineWidth Width of the line. A thin line should have a value 1.0.
	 * @param lineColor Color of the line.
	 * @param fillColor Polygon fill color.
	 */
	public void defStyle(final String name, final double lineWidth, final GamaColor lineColor,
			final GamaColor fillColor) {
		return;
	}

	private static String toHex2Digit(final int a) {
		String prefix = "";
		if (a % 256 < 16) {
			prefix = "0";
		}
		return prefix + Integer.toHexString(a % 256);
	}

	/**
	 * Produces a Object String representation of the Color given in argument
	 *
	 * @param c A Color
	 * @return A String in ABGR Hex text format
	 */
	public String kmlColor(final GamaColor c) {
		return toHex2Digit(c.alpha()) + toHex2Digit(c.blue()) + toHex2Digit(c.green()) + toHex2Digit(c.red());
	}

	/**
	 * Defines a new IconStyle to be used with addStyledRecord
	 *
	 * @param name     Style name. Must be unique.
	 * @param iconFile Name of an image (png) file. The path is relative to the
	 *                 location where the KML file will be saved.
	 * @param scale    Scale factor applied to the image. Choose 1.0 if you don't
	 *                 know.
	 * @param heading  Orientation heading of the icon. Should be a value between
	 *                 0.0 and 360.0. 0.0 is the normal orientation of the icon.
	 *                 Higher numbers apply a clockwise rotation of the icon.
	 */

	public void defIconStyle(final String name, final String iconFile, final double scale, final double heading) {
		return;
	}

	public void saveAsKml(final IScope scope, final String filename) {
		return;
	}

	public void saveAsKmz(final IScope scope, final String filename) {
		return;
	}

	public void hideFolder(final String folname) {
		final KmlFolder kf = getFolder(folname);
		kf.setVisibility(false);
	}

	public void showFolder(final String folname) {
		final KmlFolder kf = getFolder(folname);
		kf.setVisibility(true);
	}

	protected KmlFolder getDefaultFolder() {
		return null;
	}

	protected KmlFolder getFolder(final String folname) {
		return null;
	}

	protected String dateToKml(final GamaDate d) {
		return d.toISOString();
		// ("yyyy-MM-dd") + "T" + d.toString("HH:mm:ss");
	}

	public void addLabel(final IScope scope, final GamaPoint loc, final GamaDate beginDate, final GamaDate endDate,
			final String name, final String description, final String styleName) {
		getDefaultFolder().addLabel(scope, loc, dateToKml(beginDate), dateToKml(endDate), name, description, styleName);
	}

	public void addLabel(final IScope scope, final String foldname, final GamaPoint loc, final GamaDate beginDate,
			final GamaDate endDate, final String name, final String description, final String styleName) {
		getFolder(foldname).addLabel(scope, loc, dateToKml(beginDate), dateToKml(endDate), name, description,
				styleName);
	}

	public class KmlFolder {
		public Object fold;
		static final String ERR_HEADER = "Object Export: ";

		public KmlFolder(final Object doc, final String label, final String beginDate, final String endDate) {
			this.fold = null;
		}

		public KmlFolder(final Object doc, final String label) {
			this.fold = null;
		}

		/**
		 * Adds a KML Extruded Label (see https://kml-samples.googlecode.com/svn/trunk
		 * /interactive/index.html#./Point_Objects/Point_Objects.Extruded.kml)
		 *
		 * @param xpos        Latitude or X position, units depending on the CRS used in
		 *                    the model
		 * @param ypos        Longitude or Y position, units depending on the CRS used
		 *                    in the model
		 * @param height      Height at which the label should be displayed
		 * @param beginDate   Begining date of the timespan
		 * @param endDate     End date of the timespan
		 * @param name        Title (always displayed)
		 * @param description Description (only displayed on mouse click on the label)
		 */
		public void addLabel(final IScope scope, final ILocation loc, final String beginDate, final String endDate,
				final String name, final String description, final String styleName) {
			return;
		}

		public void add3DModel(final IScope scope, final ILocation loc, final double orientation, final double scale,
				final String beginDate, final String endDate, final String daefile) {
			return;
		}

		/**
		 * Add a placemark with a geometry object.The geometry can be a Point, a Line, a
		 * Polygon or any Multi-geometry. Points will be represented by an icon and
		 * linear or surface objects will be drawn.
		 *
		 * @param label     The title of the folder that will be created for this
		 *                  ShpRecord
		 * @param beginDate Begining date of the timespan
		 * @param endDate   End date of the timespan
		 * @param geom      Geometry object to be drawn
		 * @param height    Height of the feature to draw. If > 0 the feature will be
		 *                  shown extruded to the given height (relative to the ground
		 *                  level). If <= 0 the feature will be drawn flat on the
		 *                  ground.
		 */
		public void addGeometry(final IScope scope, final String label, final String beginDate, final String endDate,
				final IShape shape, final String styleName, final double height) {
			return;
		}

		public void setVisibility(final boolean value) {
			return;
		}

		public void addPoint(final Object pm, final Object point, final double height) {
		}

		public void addLine(final Object pm, final Object line, final double height) {
			return;
		}

		public void addPolygon(final Object pm, final Object poly, final double height) {
			return;
		}

		public void addMultiPoint(final Object pm, final Object mpoint, final double height) {
	return;
		}

		public void addMultiLine(final Object pm, final Object mline, final double height) {
	return;
		}

		public void addMultiPolygon(final Object pm, final Object mpoly, final double height) {
	return;
		}
	}
}