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
package org.eclipse.swtchart.extensions.theme;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class that re-applies the active e4 CSS theme to a
 * {@link ScrollableChart} without requiring callers to access
 * {@link IThemeEngine} directly.
 *
 * <p>
 * Theming is driven entirely by CSS: colours and fonts are declared in the
 * active theme's stylesheet and delivered to the chart through the registered
 * {@link ScrollableChartCSSPropertyHandler}. The supported CSS properties are:
 * <ul>
 * <li>{@code axis-color} – axis line / tick colour</li>
 * <li>{@code axis-font} – axis title font</li>
 * <li>{@code grid-color} – grid colour</li>
 * </ul>
 *
 * <p>
 * Example usage:
 *
 * <pre>
 * AxisThemeSupport.applyTheme(scrollableChart);
 * </pre>
 */
public final class AxisThemeSupport {

	private AxisThemeSupport() {

	}

	/**
	 * Re-applies the active e4 CSS theme to the given {@link ScrollableChart}
	 * by calling {@link IThemeEngine#applyStyles(Object, boolean)}.
	 *
	 * <p>
	 * This triggers the {@link ScrollableChartCSSPropertyHandler} which updates
	 * {@code axis-color}, {@code axis-font}, and {@code grid-color} on every
	 * axis of the chart.
	 *
	 * @param chart
	 *            the chart to re-theme; ignored if {@code null} or disposed
	 */
	public static void applyTheme(ScrollableChart chart) {

		if(chart == null || chart.isDisposed()) {
			return;
		}
		IThemeEngine themeEngine = PlatformUI.getWorkbench().getService(IThemeEngine.class);
		if(themeEngine != null) {
			themeEngine.applyStyles(chart, true);
		}
	}
}
