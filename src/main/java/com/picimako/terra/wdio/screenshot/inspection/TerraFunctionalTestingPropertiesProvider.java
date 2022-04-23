//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.MISMATCH_TOLERANCE;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.RULES;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.SELECTOR;

import java.util.List;

/**
 * Terra-functional-testing specific properties provider.
 *
 * @since 0.6.0
 */
public class TerraFunctionalTestingPropertiesProvider implements TerraPropertiesProvider {

    private static final List<String> SCREENSHOT_PROPERTIES = List.of(MISMATCH_TOLERANCE, SELECTOR);
    private static final List<String> ELEMENT_PROPERTIES = List.of(SELECTOR, MISMATCH_TOLERANCE, RULES);
    private static final List<String> ACCESSIBILITY_PROPERTIES = List.of(RULES);

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
