/*
 * Copyright 2021 Tamás Balog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 */
public class TerraToolkitPropertiesProvider implements TerraPropertiesProvider {

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
