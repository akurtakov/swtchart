/*******************************************************************************
 * Copyright (c) 2025, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - axis-color and axis-font CSS properties
 *******************************************************************************/
package org.eclipse.swtchart.extensions.theme;

import org.eclipse.e4.ui.css.core.dom.properties.ICSSPropertyHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.WidgetElement;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.w3c.dom.css.CSSValue;

/**
 * CSS property handler for {@link ScrollableChart}.
 * <p>
 * Supported CSS properties:
 * <ul>
 * <li>{@code grid-color} – sets the grid colour on all axes</li>
 * <li>{@code axis-color} – sets the axis line/tick colour on all axes</li>
 * <li>{@code axis-font} – sets the axis title font on all axes</li>
 * </ul>
 */
@SuppressWarnings("restriction")
public class ScrollableChartCSSPropertyHandler implements ICSSPropertyHandler {

	@Override
	public boolean applyCSSProperty(Object element, String property, CSSValue value, String pseudo, CSSEngine engine) throws Exception {

		switch(property) {
			case "grid-color": //$NON-NLS-1$
				applyCSSPropertyGridColor(element, value, engine);
				break;
			case "axis-color": //$NON-NLS-1$
				applyCSSPropertyAxisColor(element, value, engine);
				break;
			case "axis-font": //$NON-NLS-1$
				applyCSSPropertyAxisFont(element, value, engine);
				break;
			default:
				break;
		}
		return false;
	}

	private ScrollableChart getScrollableChart(Object element) {

		Widget widget = (Widget)((WidgetElement)element).getNativeWidget();
		return (ScrollableChart)widget;
	}

	private void applyCSSPropertyGridColor(Object element, CSSValue value, CSSEngine engine) throws Exception {

		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			ScrollableChart scrollableChart = getScrollableChart(element);
			Color color = (Color)engine.convert(value, Color.class, scrollableChart.getDisplay());
			IChartSettings chartSettings = scrollableChart.getChartSettings();
			chartSettings.getPrimaryAxisSettingsX().setGridColor(color);
			chartSettings.getPrimaryAxisSettingsY().setGridColor(color);
			for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListX()) {
				settings.setGridColor(color);
			}
			for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListY()) {
				settings.setGridColor(color);
			}
		}
	}

	private void applyCSSPropertyAxisColor(Object element, CSSValue value, CSSEngine engine) throws Exception {

		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			ScrollableChart scrollableChart = getScrollableChart(element);
			Color color = (Color)engine.convert(value, Color.class, scrollableChart.getDisplay());
			IChartSettings chartSettings = scrollableChart.getChartSettings();
			setAxisColor(chartSettings.getPrimaryAxisSettingsX(), color);
			setAxisColor(chartSettings.getPrimaryAxisSettingsY(), color);
			for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListX()) {
				setAxisColor(settings, color);
			}
			for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListY()) {
				setAxisColor(settings, color);
			}
		}
	}

	private void applyCSSPropertyAxisFont(Object element, CSSValue value, CSSEngine engine) throws Exception {

		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			ScrollableChart scrollableChart = getScrollableChart(element);
			Font font = (Font)engine.convert(value, Font.class, scrollableChart.getDisplay());
			IChartSettings chartSettings = scrollableChart.getChartSettings();
			setAxisFont(chartSettings.getPrimaryAxisSettingsX(), font);
			setAxisFont(chartSettings.getPrimaryAxisSettingsY(), font);
			for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListX()) {
				setAxisFont(settings, font);
			}
			for(ISecondaryAxisSettings settings : chartSettings.getSecondaryAxisSettingsListY()) {
				setAxisFont(settings, font);
			}
		}
	}

	private void setAxisColor(IAxisSettings axisSettings, Color color) {

		if(color != null) {
			axisSettings.setColor(color);
		}
	}

	private void setAxisFont(IAxisSettings axisSettings, Font font) {

		if(font != null) {
			axisSettings.setTitleFont(font);
		}
	}
}
