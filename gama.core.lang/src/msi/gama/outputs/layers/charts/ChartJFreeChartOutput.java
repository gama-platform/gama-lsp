/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutput.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartJFreeChartOutput extends ChartOutput {

	private BufferedImage createCompatibleImage(final int sizeX, final int sizeY) {
		if ((int) r.getWidth() != sizeX || (int) r.getHeight() != sizeY) {
			r.setRect(0, 0, sizeX, sizeY);
			if (cache != null) {
				cache.flush();
			}
			cache = ImageUtils.createCompatibleImage(sizeX, sizeY, false);
		}
		return cache;
	}

	public static final Shape[] defaultmarkers = null;
	boolean oldAntiAlias;

	public Object info;
	ArrayList<Object> jfreedataset = new ArrayList<>();
	Object chart = null;
	Rectangle2D r = new Rectangle2D.Double();
	BufferedImage cache;
	Object defaultrenderer;
	HashMap<String, Integer> IdPosition = new HashMap<>(); // serie
															// id-nb
															// for
															// arraylists/table
															// requirements
	HashMap<String, Object> RendererSet = new HashMap<>(); // one
															// renderer
															// for
															// each
															// serie
	int nbseries = 0; // because there is always one dataset, so it is difficult
	// A filed aiming at controlling the rate at which charts can be produced.
	// Directly controlled by the
	// ChartProgressListener
	private volatile boolean ready = true;
	// to count...

	public ChartJFreeChartOutput(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		info = new Object();
	}

	public static ChartJFreeChartOutput createChartOutput(final IScope scope, final String name,
			final IExpression typeexp) {
		ChartJFreeChartOutput newChart;
		int type = SERIES_CHART;

		final IExpression string1 = typeexp;
		if (string1 != null) {
			final String t = Cast.asString(scope, string1.value(scope));
			type = IKeyword.SERIES.equals(t) ? SERIES_CHART
					: IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
							: IKeyword.PIE.equals(t) ? PIE_CHART
									: IKeyword.RADAR.equals(t) ? RADAR_CHART
											: IKeyword.HEATMAP.equals(t) ? HEATMAP_CHART
													: IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
															: IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;

		}

		switch (type) {

		case PIE_CHART: {
			newChart = new ChartJFreeChartOutputPie(scope, name, typeexp);
			break;
		}
		case HISTOGRAM_CHART:
		case BOX_WHISKER_CHART: {
			newChart = new ChartJFreeChartOutputHistogram(scope, name, typeexp);
			break;
		}
		case RADAR_CHART: {
			newChart = new ChartJFreeChartOutputRadar(scope, name, typeexp);
			break;
		}
		case HEATMAP_CHART: {
			newChart = new ChartJFreeChartOutputHeatmap(scope, name, typeexp);
			break;
		}
		default: {
			newChart = new ChartJFreeChartOutputScatter(scope, name, typeexp);
		}
		}
		return newChart;
	}

	private Graphics2D getGraphics(final int sizeX, final int sizeY) {
		return createCompatibleImage(sizeX, sizeY).createGraphics();
	}

	@Override
	public BufferedImage getImage(final int sizeX, final int sizeY, final boolean antiAlias) {
		return null;
	}

	public void chartProgress(final Object event) {
		return;
	}

	@Override
	public void draw(final Graphics2D g2D, final Rectangle2D area, final boolean antiAlias) {
		return;
	}

	@Override
	public void step(final IScope scope) {
		chartdataset.updatedataset(scope, getChartCycle(scope));
		if (!ready) {
			return;
		}
		updateOutput(scope);
	}

	protected void initRenderer(final IScope scope) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initChart(final IScope scope, final String chartname) {
		return;
	}

	Object getOrCreateRenderer(final IScope scope, final String serieid) {
		return null;
	}

	protected Object createRenderer(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub
		return new Object();

	}

	Font getLabelFont() {
		return new Font(labelFontFace, labelFontStyle, labelFontSize);
	}

	Font getTickFont() {
		return new Font(tickFontFace, tickFontStyle, tickFontSize);
	}

	Font getLegendFont() {
		return new Font(legendFontFace, legendFontStyle, legendFontSize);
	}

	Font getTitleFont() {
		return new Font(titleFontFace, titleFontStyle, titleFontSize);
	}

	@Override
	protected void updateImage(final IScope scope) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object getJFChart() {
		return chart;
	}

	@Override
	public void dispose(final IScope scope) {
		if (cache != null) {
			cache.flush();
		}
		cache = null;
		clearDataSet(scope);
		jfreedataset.clear();
		chart = null;
	}

}
