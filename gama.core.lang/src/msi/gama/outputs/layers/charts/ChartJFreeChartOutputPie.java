/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputPie.java, in plugin msi.gama.core, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8.1)
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
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputPie extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputPie(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stubs

	}

	@Override
	public void createChart(final IScope scope) {
		return;
	}

	@Override
	public void initdataset() {
		super.initdataset();
		if (getType() == ChartOutput.PIE_CHART) {
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
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
		default: {
			source.setCumulative(scope, false); // never cumulative by default
			source.setUseSize(scope, false);
		}
		}

	}

	@Override
	public void initChart(final IScope scope, final String chartname) {
		return;
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
	protected void resetSerie(final IScope scope, final String serieid) {
		return;
	}

	@Override
	protected void initRenderer(final IScope scope) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {
		return;
	}

}
