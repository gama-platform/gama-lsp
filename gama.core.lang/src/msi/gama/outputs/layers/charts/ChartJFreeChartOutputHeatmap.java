/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputHeatmap.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Point;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;


import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputHeatmap extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputHeatmap(final IScope scope, final String name, final IExpression typeexp) {
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
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12: {
			source.setCumulative(scope, false);
			source.setCumulativeY(scope, true);
			source.setUseSize(scope, true);
			break;
		}
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
		case ChartDataSource.DATA_TYPE_LIST_POINT:
		case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
		default: {
			source.setCumulative(scope, false);
			source.setUseSize(scope, true);
		}
		}

	}

	@Override
	public void initdataset() {
		super.initdataset();
		chartdataset.setCommonXSeries(true);
		chartdataset.setCommonYSeries(true);
		chartdataset.setByCategory(false);
		chartdataset.forceNoXAccumulate = true;
		chartdataset.forceNoYAccumulate = true;
	}

	@Override
	protected Object createRenderer(final IScope scope, final String serieid) {
		return null;
	}

	protected static final Object createLUT(final int ncol, final float vmin, final float vmax,
			final Color start, final Color med, final Color end) {
		return null;
	}

	protected static final Object createLUT(final int ncol, final float vmin, final float vmax,
			final Color start, final Color end) {
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
	public void preResetSeries(final IScope scope) {
		return;
	}

	@Override
	protected void resetSerie(final IScope scope, final String serieid) {
		return;
	}

	@Override
	public void resetAxes(final IScope scope) {
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
	public void setUseYLabels(final IScope scope, final IExpression expval) {
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
