/*******************************************************************************
 * Copyright (c) 2026 SWTChart project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtchart.extensions.examples.charts;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.RangeRestriction.ExtendType;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ICompressionSupport;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.marker.LabelMarker;

/**
 * Simulated HPLC-UV chromatogram with a gradient baseline drift, an injection
 * peak (positive spike followed by a negative refractive index dip at the void
 * time), a negative system peak and a couple of tailing analyte peaks.
 */
public class HPLCDemoChart {

	private static final double RUN_TIME = 15.0; // minutes
	private static final double SAMPLING_RATE = 10.0; // Hz
	/*
	 * Analyte peaks: retention time [min], height [mAU], sigma [min], tailing factor
	 */
	private static final double[][] PEAKS = { //
			{2.35, 85.0, 0.030, 1.3}, //
			{3.10, 42.0, 0.035, 1.2}, //
			{4.85, 120.0, 0.040, 1.4}, //
			{5.20, 64.0, 0.042, 1.3}, //
			{7.60, 210.0, 0.050, 1.5}, //
			{9.45, 33.0, 0.055, 1.2}, //
			{11.30, 150.0, 0.060, 1.6}, //
			{12.90, 18.0, 0.065, 1.1} //
	};
	private static final String[] LABELS = { //
			"Caffeine", //
			"Theobromine", //
			"Paracetamol", //
			"Acetylsalicylic acid", //
			"Ibuprofen", //
			"Naproxen", //
			"Diclofenac", //
			"Impurity A" //
	};

	public static void main(String args[]) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("DemoChart_HPLC");
		shell.setSize(1200, 500);
		shell.setLayout(new FillLayout());

		LineChart lineChart = new LineChart(shell, SWT.NONE);
		lineChart.setFileName("DemoChart_HPLC");
		shell.open();

		IChartSettings chartSettings = lineChart.getChartSettings();
		chartSettings.setTitle("HPLC-UV Chromatogram (254 nm)");
		chartSettings.setTitleVisible(true);
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false); // show the negative signals
		rangeRestriction.setRestrictSelectX(true);
		rangeRestriction.setExtendTypeY(ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY(0.05d);
		rangeRestriction.setExtendMaxY(0.5d); // room for the peak labels
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Retention Time [min]");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setGridLineStyle(LineStyle.NONE);
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Absorbance [mAU]");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat("0", new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setGridLineStyle(LineStyle.DOT);
		lineChart.applySettings(chartSettings);

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		lineSeriesDataList.add(createChromatogram(new Color(0, 90, 180)));
		lineSeriesDataList.add(createZeroLine(new Color(160, 160, 160)));
		lineSeriesDataList.add(createPeakApexes(new Color(200, 0, 0)));
		lineChart.addSeriesData(lineSeriesDataList, ICompressionSupport.NO_COMPRESSION);

		LabelMarker labelMarker = new LabelMarker(lineChart.getBaseChart());
		labelMarker.setLabels(List.of(LABELS), 2, SWT.VERTICAL);
		lineChart.getBaseChart().getPlotArea().addCustomPaintListener(labelMarker);

		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static ILineSeriesData createChromatogram(Color color) {

		int size = (int)(RUN_TIME * 60.0 * SAMPLING_RATE) + 1;
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		Random random = new Random(42);

		for(int i = 0; i < size; i++) {
			double t = i / (60.0 * SAMPLING_RATE);
			double y = baseline(t);
			/*
			 * Injection peak at the void time: a short positive spike
			 * immediately followed by a broader negative dip caused by the
			 * refractive index mismatch of the sample solvent.
			 */
			y += gaussian(t, 0.95, 28.0, 0.015);
			y -= gaussian(t, 1.08, 35.0, 0.045);
			y += gaussian(t, 1.22, 6.0, 0.030);
			/*
			 * Negative system peak caused by the mobile phase additive.
			 */
			y -= gaussian(t, 6.40, 12.0, 0.070);
			for(double[] peak : PEAKS) {
				y += tailingPeak(t, peak[0], peak[1], peak[2], peak[3]);
			}
			y += random.nextGaussian() * 0.25; // detector noise
			xSeries[i] = t;
			ySeries[i] = y;
		}

		SeriesData seriesData = new SeriesData(xSeries, ySeries, "Chromatogram");
		LineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setEnableArea(false);
		return lineSeriesData;
	}

	private static ILineSeriesData createZeroLine(Color color) {

		SeriesData seriesData = new SeriesData(new double[]{0.0, RUN_TIME}, new double[]{0.0, 0.0}, "Zero");
		LineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(LineStyle.DASH);
		lineSeriesSettings.setEnableArea(false);
		return lineSeriesData;
	}

	private static ILineSeriesData createPeakApexes(Color color) {

		double[] xSeries = new double[PEAKS.length];
		double[] ySeries = new double[PEAKS.length];
		for(int i = 0; i < PEAKS.length; i++) {
			double t = PEAKS[i][0];
			xSeries[i] = t;
			ySeries[i] = baseline(t) + PEAKS[i][1];
		}

		SeriesData seriesData = new SeriesData(xSeries, ySeries, "Peaks");
		LineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
		lineSeriesSettings.setSymbolSize(4);
		lineSeriesSettings.setSymbolColor(color);
		lineSeriesSettings.setEnableArea(false);
		return lineSeriesData;
	}

	/**
	 * Slow upward drift caused by the increasing organic fraction of the
	 * gradient.
	 */
	private static double baseline(double t) {

		return 1.5 + 0.35 * t + 0.02 * t * t;
	}

	private static double gaussian(double t, double center, double height, double sigma) {

		double dt = t - center;
		return height * Math.exp(-(dt * dt) / (2.0 * sigma * sigma));
	}

	/**
	 * Asymmetric Gaussian whose trailing edge is broadened by the tailing
	 * factor, as typically observed for basic analytes on silica columns.
	 */
	private static double tailingPeak(double t, double center, double height, double sigma, double tailing) {

		double width = t > center ? sigma * tailing : sigma;
		return gaussian(t, center, height, width);
	}
}
