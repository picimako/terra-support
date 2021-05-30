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
@Service
public final class TerraToolkitManager extends TerraResourceManager {

    private ScreenshotNameResolver nameResolver;
    private ScreenshotContextParser contextParser;
    private ScreenshotContextParser contextParserWithSeparator;
    private TerraPropertiesProvider propertiesProvider;

    //Required for project service creation
    public TerraToolkitManager(Project project) {
    }

    @Override
    public ScreenshotNameResolver screenshotNameResolver() {
        return Optional.ofNullable(nameResolver).orElseGet(() -> nameResolver = new TerraToolkitScreenshotNameResolver());
    }

    @Override
    public ScreenshotContextParser screenshotContextParser() {
        return Optional.ofNullable(contextParser).orElseGet(() -> contextParser = new TerraToolkitScreenshotContextParser());
    }

    @Override
    public ScreenshotContextParser screenshotContextParser(String contextSeparator) {
        return Optional.ofNullable(contextParserWithSeparator).orElseGet(() -> contextParserWithSeparator = new TerraToolkitScreenshotContextParser(contextSeparator));
    }

    @Override
    public SpecFolderCollector specFolderCollector() {
        return SpecFolderCollector.TERRA_TOOLKIT_SPEC_COLLECTOR;
    }

    @Override
    public TerraPropertiesProvider screenshotValidationProperties() {
        return Optional.ofNullable(propertiesProvider).orElseGet(() -> propertiesProvider = new TerraToolkitPropertiesProvider());
    }
}