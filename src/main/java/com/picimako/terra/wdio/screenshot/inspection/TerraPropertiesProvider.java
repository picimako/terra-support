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
