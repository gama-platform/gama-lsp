/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputScatter.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputScatter extends ChartJFreeChartOutput {

	public class myXYErrorRenderer {

		ChartJFreeChartOutputScatter myoutput;
		String myid;
		boolean useSize;
		AffineTransform transform = new AffineTransform();

		public boolean isUseSize() {
			return useSize;
		}

		public void setUseSize(final IScope scope, final boolean useSize) {
			this.useSize = useSize;

		}

		public void setMyid(final String myid) {
			this.myid = myid;
		}

		private static final long serialVersionUID = 1L;

		public void setOutput(final ChartJFreeChartOutput output) {
			myoutput = (ChartJFreeChartOutputScatter) output;
		}

		public Shape getItemShape(final int row, final int col) {
			return null;
		}
	}

	double getScale(final String serie, final int col) {
		if (MarkerScale.containsKey(serie)) {
			return MarkerScale.get(serie).get(col);
		}
		return 1;
	}

	HashMap<String, ArrayList<Double>> MarkerScale = new HashMap<>();

	public ChartJFreeChartOutputScatter(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void createChart(final IScope scope) {
		super.createChart(scope);
	}

	@Override
	public void setDefaultPropertiesFromType(final IScope scope, final ChartDataSource source, final int type_val) {
		// TODO Auto-generated method stub

		switch (type_val) {
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
		case ChartDataSource.DATA_TYPE_LIST_POINT:
		case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE: {
			source.setCumulative(scope, false);
			source.setUseSize(scope, false);
			break;
		}
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3: {
			source.setCumulative(scope, true);
			source.setUseSize(scope, true);
			break;

		}
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3: {
			source.setCumulative(scope, false);
			source.setUseSize(scope, true);
			break;

		}
		default: {
			source.setCumulative(scope, true);
			source.setUseSize(scope, false);
		}
		}

	}

	@Override
	public void initdataset() {
		super.initdataset();
		if (getType() == ChartOutput.SERIES_CHART) {
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(false);
		}
		if (getType() == ChartOutput.XY_CHART) {
			chartdataset.setCommonXSeries(false);
			chartdataset.setByCategory(false);
		}
		if (getType() == ChartOutput.SCATTER_CHART) {
			chartdataset.setCommonXSeries(false);
			chartdataset.setByCategory(false);
		}
	}

	@Override
	protected Object createRenderer(final IScope scope, final String serieid) {
		return null;
	}

	protected void resetRenderer(final IScope scope, final String serieid) {
		return;
	}

	@Override
	protected void clearDataSet(final IScope scope) {
		return;
	}

	@Override
	protected void createNewSerie(final IScope scope, final String serieid) {
		return;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void resetSerie(final IScope scope, final String serieid) {
		return;
	}

	public Object formatYAxis(final IScope scope, final Object axis) {
		return null;
	}

	@Override
	public void resetAxes(final IScope scope) {
		return;
	}

	@Override
	public void setSerieMarkerShape(final IScope scope, final String serieid, final String markershape) {
		return;
	}

	@Override
	public void setUseSize(final IScope scope, final String name, final boolean b) {
		return;
	}

	@Override
	protected void initRenderer(final IScope scope) {
		return;
	}

	@Override
	public void setUseXSource(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis

	}

	@Override
	public void setUseXLabels(final IScope scope, final IExpression expval) {
		return;
	}

	@Override
	public void initChart(final IScope scope, final String chartname) {
		return;
	}

	@Override
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {
		return;
	}

}
