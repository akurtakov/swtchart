/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtchart.extensions.examples.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.examples.support.SeriesConverter;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.theme.AxisThemeSupport;

import jakarta.inject.Inject;

/**
 * Demonstrates {@link AxisThemeSupport#applyTheme(LineChart)} in action.
 * <p>
 * Press the <em>Apply Theme</em> button to re-apply the active e4 CSS theme to
 * the embedded chart. The CSS engine routes the call through
 * {@code ScrollableChartCSSPropertyHandler}, which updates
 * {@code axis-color}, {@code axis-font}, and {@code grid-color} on every axis.
 */
public class LineSeries_ThemeEngine_Part extends Composite {

	private LineChart lineChart;

	@Inject
	public LineSeries_ThemeEngine_Part(Composite parent) {

		super(parent, SWT.NONE);
		initialize();
	}

	private void initialize() {

		setLayout(new GridLayout(1, true));
		/*
		 * Toolbar
		 */
		Composite toolbar = new Composite(this, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		toolbar.setLayout(new GridLayout(1, false));

		Button applyThemeButton = new Button(toolbar, SWT.PUSH);
		applyThemeButton.setText("Apply Theme");
		applyThemeButton.setToolTipText("Re-apply the active e4 CSS theme via AxisThemeSupport");
		applyThemeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				AxisThemeSupport.applyTheme(lineChart);
				lineChart.applySettings(lineChart.getChartSettings());
			}
		});
		/*
		 * Chart
		 */
		lineChart = new LineChart(this, SWT.BORDER);
		lineChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		/*
		 * Series
		 */
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		ISeriesData seriesData = SeriesConverter.getSeriesXY(SeriesConverter.LINE_SERIES_2);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setEnableArea(true);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		lineSeriesDataList.add(lineSeriesData);
		lineChart.addSeriesData(lineSeriesDataList);
		/*
		 * Apply the active CSS theme immediately so the chart is already
		 * themed when the part first becomes visible.
		 */
		AxisThemeSupport.applyTheme(lineChart);
	}
}
