/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputRadar.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
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
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputRadar extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputRadar(final IScope scope, final String name, final IExpression typeexp) {
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
		chartdataset.setCommonXSeries(true);
		chartdataset.setByCategory(true);
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
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
		default: {
			source.setCumulative(scope, false); // never cumulative by default
			source.setUseSize(scope, false);
		}
		}

	}

	Object createDataset(final IScope scope) {
		return null;
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
		// final ChartDataSeries dataserie = chartdataset.getDataSeries(scope,
		// serieid);
		// final XYIntervalSeries serie = new
		// XYIntervalSeries(dataserie.getSerieLegend(scope), false, true);
		if (!IdPosition.containsKey(serieid)) {
			return;
		}
		// DEBUG.LOG("new serie"+serieid+" at
		// "+IdPosition.get(serieid)+" fdsize "+plot.getCategories().size()+"
		// jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount()+" nbse
		// "+nbseries);
		// TODO Auto-generated method stub
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
		if (this.series_label_position.equals("none")) {
			return;
		}

	}

	private void resetDomainAxis(final IScope scope) {
		return;
	}

	@Override
	public void initChart(final IScope scope, final String chartname) {
		return;
	}

	@Override
	public void initChart_post_data_init(final IScope scope) {
		return;
	}

	@Override
	protected void initRenderer(final IScope scope) {

	}

	@Override
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {
		return;
	}

}
