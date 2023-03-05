//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import com.picimako.terra.wdio.screenshot.ScreenshotNameResolver;
import com.picimako.terra.wdio.screenshot.inspection.TerraPropertiesProvider;

/**
 * Provides terra resources handlers based on which terra testing library is used in the project.
 */
public interface TerraResourceHandlerProvider {

    ScreenshotNameResolver screenshotNameResolver();

    ScreenshotContextParser screenshotContextParser();

    ScreenshotContextParser screenshotContextParser(String contextSeparator);

    SpecFolderCollector specFolderCollector();

    TerraPropertiesProvider screenshotValidationProperties();
}
