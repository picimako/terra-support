//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.AXE_RULES;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.MIS_MATCH_TOLERANCE;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.SELECTOR;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.VIEWPORTS;

import java.util.List;

/**
 * Terra-toolkit specific properties provider.
 *
 * @since 0.6.0
 * @see com.picimako.terra.wdio.TerraToolkitManager
 */
public class TerraToolkitPropertiesProvider implements TerraPropertiesProvider {
    public static final TerraToolkitPropertiesProvider INSTANCE = new TerraToolkitPropertiesProvider();

    private static final List<String> SCREENSHOT_PROPERTIES = List.of(MIS_MATCH_TOLERANCE, SELECTOR, VIEWPORTS);
    private static final List<String> ELEMENT_PROPERTIES = List.of(SELECTOR, MIS_MATCH_TOLERANCE, AXE_RULES);
    private static final List<String> ACCESSIBILITY_PROPERTIES = List.of(AXE_RULES);

    @Override
    public List<String> screenshotProperties() {
        return SCREENSHOT_PROPERTIES;
    }

    @Override
    public List<String> elementProperties() {
        return ELEMENT_PROPERTIES;
    }

    @Override
    public List<String> accessibilityProperties() {
        return ACCESSIBILITY_PROPERTIES;
    }
}
