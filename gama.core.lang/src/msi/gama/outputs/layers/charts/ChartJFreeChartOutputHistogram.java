/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputHistogram.java, in plugin msi.gama.core, is part of the source
 * code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Point;
import java.util.ArrayList;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputHistogram extends ChartJFreeChartOutput {

	boolean useSubAxis = false;
	boolean useMainAxisLabel = true;

	public static void enableFlatLook(final boolean flat) {
		return;
	}

	static {
		enableFlatLook(GamaPreferences.Displays.CHART_FLAT.getValue());
		GamaPreferences.Displays.CHART_FLAT.onChange(newValue -> enableFlatLook(newValue));
	}

	public ChartJFreeChartOutputHistogram(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void createChart(final IScope scope) {
		return;
	}

	@Override
	public void initdataset() {
		super.initdataset();
		if (getType() == ChartOutput.HISTOGRAM_CHART) {
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(true);
		}
	}

	@Override
	public void setDefaultPropertiesFromType(final IScope scope, final ChartDataSource source, final int type_val) {
		// TODO Auto-generated method stub

		switch (type_val) {
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
		case ChartDataSource.DATA_TYPE_LIST_POINT:
		case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE:
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3: {
			source.setCumulative(scope, false);
			source.setUseSize(scope, false);
			break;

		}
		default: {
			source.setCumulative(scope, false); // never cumulative by default
			source.setUseSize(scope, false);
		}
		}

	}

	static class LabelGenerator {
		/**
		 * Generates an item label.
		 *
		 * @param dataset  the dataset.
		 * @param series   the series index.
		 * @param category the category index.
		 *
		 * @return the label.
		 */
		public String generateLabel(final Object dataset, final int series, final int category) {
			return null;
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

	@Override
	public void removeSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub
		super.removeSerie(scope, serieid);
		this.clearDataSet(scope);
	}

	@Override
	protected void resetSerie(final IScope scope, final String serieid) {
		return;
	}

	@Override
	public void resetAxes(final IScope scope) {
		return;
	}

	public void resetDomainAxis(final IScope scope) {
		return;
	}
	//
	// @Override
	// public void initChart(final IScope scope, final String chartname) {
	// super.initChart(scope, chartname);
	// // final CategoryPlot pp = (CategoryPlot) chart.getPlot();
	//
	// }

	@Override
	public void initChart_post_data_init(final IScope scope) {
		return;
	}

	@Override
	protected void initRenderer(final IScope scope) {
		// final CategoryPlot pp = (CategoryPlot) chart.getPlot();
		// final BarRenderer renderer = (BarRenderer) pp.getRenderer();

		// TODO Auto-generated method stub
		// CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
		// defaultrenderer = new BarRenderer();
		// plot.setRenderer((BarRenderer)defaultrenderer);

	}

	@Override
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {
		return;
	}

}
