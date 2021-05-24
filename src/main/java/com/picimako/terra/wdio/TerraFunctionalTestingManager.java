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
import com.picimako.terra.wdio.screenshot.TerraFunctionalTestingScreenshotNameResolver;

/**
 * Provides terra-functional-testing specific resource handlers.
 */
@Service
public final class TerraFunctionalTestingManager extends TerraResourceManager {

    private ScreenshotNameResolver nameResolver;
    private ScreenshotContextParser contextParser;
    private ScreenshotContextParser contextParserWithSeparator;

    //Required for project service creation
    public TerraFunctionalTestingManager(Project project) {
    }

    @Override
    public ScreenshotNameResolver screenshotNameResolver() {
        return Optional.ofNullable(nameResolver).orElseGet(() -> nameResolver = new TerraFunctionalTestingScreenshotNameResolver());
    }

    @Override
    public ScreenshotContextParser screenshotContextParser() {
        return Optional.ofNullable(contextParser).orElseGet(() -> contextParser = new TerraFunctionalTestingScreenshotContextParser());
    }

    @Override
    public ScreenshotContextParser screenshotContextParser(String contextSeparator) {
        return Optional.ofNullable(contextParserWithSeparator).orElseGet(() -> contextParserWithSeparator = new TerraFunctionalTestingScreenshotContextParser(contextSeparator));
    }

    @Override
    public SpecFolderCollector specFolderCollector() {
        return SpecFolderCollector.TERRA_FUNCTIONAL_TESTING_SPEC_COLLECTOR;
    }
}
