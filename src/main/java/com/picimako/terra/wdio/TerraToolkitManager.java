//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import java.util.Optional;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import com.picimako.terra.wdio.screenshot.ScreenshotNameResolver;
import com.picimako.terra.wdio.screenshot.TerraToolkitScreenshotNameResolver;
import com.picimako.terra.wdio.screenshot.inspection.TerraPropertiesProvider;
import com.picimako.terra.wdio.screenshot.inspection.TerraToolkitPropertiesProvider;

/**
 * Provides terra-toolkit specific resource handlers.
 */
@Service(Service.Level.PROJECT)
public final class TerraToolkitManager extends TerraResourceManager {

    private ScreenshotContextParser contextParserWithSeparator;

    //Required for project service creation
    public TerraToolkitManager(Project project) {
    }

    @Override
    public ScreenshotNameResolver screenshotNameResolver() {
        return TerraToolkitScreenshotNameResolver.INSTANCE;
    }

    @Override
    public ScreenshotContextParser screenshotContextParser() {
        return TerraToolkitScreenshotContextParser.INSTANCE;
    }

    @Override
    public ScreenshotContextParser screenshotContextParser(String contextSeparator) {
        return Optional.ofNullable(contextParserWithSeparator)
            .orElseGet(() -> contextParserWithSeparator = new TerraToolkitScreenshotContextParser(contextSeparator));
    }

    @Override
    public SpecFolderCollector specFolderCollector() {
        return SpecFolderCollector.TERRA_TOOLKIT_SPEC_COLLECTOR;
    }

    @Override
    public TerraPropertiesProvider screenshotValidationProperties() {
        return TerraToolkitPropertiesProvider.INSTANCE;
    }
}
