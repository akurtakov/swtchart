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

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

/**
 * Utility class that applies Eclipse workbench theme colours and fonts to
 * {@link IAxisSettings} objects without requiring callers to access
 * {@code PlatformUI} or the {@link IThemeManager} directly.
 *
 * <p>
 * The key convention used by this class matches the one established by the
 * chemclipse {@code ChartSupport.themeAxis()} helper:
 * <ul>
 * <li>{@code <part>.LineColor} – axis line / tick colour
 * ({@link IAxisSettings#setColor(Color)})</li>
 * <li>{@code <part>.GridColor} – grid colour
 * ({@link IAxisSettings#setGridColor(Color)})</li>
 * <li>{@code <part>.Font} – axis title font
 * ({@link IAxisSettings#setTitleFont(Font)})</li>
 * </ul>
 *
 * <p>
 * Downstream callers (e.g. chemclipse) can replace
 *
 * <pre>
 * IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
 * ITheme currentTheme = themeManager.getCurrentTheme();
 * ColorRegistry colorRegistry = currentTheme.getColorRegistry();
 * axisSettings.setColor(colorRegistry.get(part + ".LineColor"));
 * axisSettings.setGridColor(colorRegistry.get(part + ".GridColor"));
 * FontRegistry fontRegistry = currentTheme.getFontRegistry();
 * axisSettings.setTitleFont(fontRegistry.get(part + ".Font"));
 * </pre>
 *
 * with a single call:
 *
 * <pre>
 * AxisThemeSupport.applyTheme(axisSettings, part);
 * </pre>
 */
public final class AxisThemeSupport {

	/** Suffix for the axis line/tick colour theme key. */
	public static final String SUFFIX_LINE_COLOR = ".LineColor"; //$NON-NLS-1$
	/** Suffix for the axis grid colour theme key. */
	public static final String SUFFIX_GRID_COLOR = ".GridColor"; //$NON-NLS-1$
	/** Suffix for the axis title font theme key. */
	public static final String SUFFIX_FONT = ".Font"; //$NON-NLS-1$

	private AxisThemeSupport() {

	}

	/**
	 * Applies the Eclipse workbench theme entries identified by {@code part} to
	 * the given {@link IAxisSettings}.
	 *
	 * <p>
	 * Only non-{@code null} registry values are applied; if a key is not
	 * registered in the current theme the corresponding axis setting is left
	 * unchanged.
	 *
	 * @param axisSettings
	 *            the axis settings to update (must not be {@code null})
	 * @param part
	 *            the theme key prefix (e.g. {@code "org.foo.MyAxis"})
	 * @throws IllegalStateException
	 *             if the workbench has not been started yet
	 */
	public static void applyTheme(IAxisSettings axisSettings, String part) {

		if(axisSettings == null || part == null) {
			return;
		}
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		ColorRegistry colorRegistry = currentTheme.getColorRegistry();
		Color lineColor = colorRegistry.get(part + SUFFIX_LINE_COLOR);
		if(lineColor != null) {
			axisSettings.setColor(lineColor);
		}
		Color gridColor = colorRegistry.get(part + SUFFIX_GRID_COLOR);
		if(gridColor != null) {
			axisSettings.setGridColor(gridColor);
		}
		FontRegistry fontRegistry = currentTheme.getFontRegistry();
		Font font = fontRegistry.get(part + SUFFIX_FONT);
		if(font != null) {
			axisSettings.setTitleFont(font);
		}
	}

	/**
	 * Convenience method that applies the same theme prefix to every axis (both
	 * primary and all secondary axes) in the given {@link IChartSettings}.
	 *
	 * @param chartSettings
	 *            the chart settings to update (must not be {@code null})
	 * @param part
	 *            the theme key prefix (e.g. {@code "org.foo.MyAxis"})
	 */
	public static void applyTheme(IChartSettings chartSettings, String part) {

		if(chartSettings == null || part == null) {
			return;
		}
		applyTheme(chartSettings.getPrimaryAxisSettingsX(), part);
		applyTheme(chartSettings.getPrimaryAxisSettingsY(), part);
		for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListX()) {
			applyTheme(settings, part);
		}
		for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListY()) {
			applyTheme(settings, part);
		}
	}
}
