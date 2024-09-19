//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import java.util.List;

/**
 * Provides Terra specific JS property values for various helpers.
 * <p>
 * Although the properties could be retrieved as a Set, to keep the order of them fix and deterministic, List is used
 * instead.
 *
 * @since 0.6.0
 */
public interface TerraPropertiesProvider {

    /**
     * For Terra.it.matchesScreenshot and Terra.validates.screenshot.
     */
    List<String> screenshotProperties();

    /**
     * For Terra.it.validatesElement and Terra.validates.element.
     */
    List<String> elementProperties();

    /**
     * For Terra.it.isAccessible and Terra.validates.accessibility.
     */
    List<String> accessibilityProperties();
}
