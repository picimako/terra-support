//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import java.util.Optional;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import com.picimako.terra.wdio.screenshot.ScreenshotNameResolver;
import com.picimako.terra.wdio.screenshot.TerraFunctionalTestingScreenshotNameResolver;
import com.picimako.terra.wdio.screenshot.inspection.TerraPropertiesProvider;
import com.picimako.terra.wdio.screenshot.inspection.TerraFunctionalTestingPropertiesProvider;

/**
 * Provides terra-functional-testing specific resource handlers.
 */
@Service(Service.Level.PROJECT)
public final class TerraFunctionalTestingManager extends TerraResourceManager {

    /**
     * Retrieves the folder type based on the {@code [type]/theme/locale/browser_viewport/spec} folder structure.
     */
    private static final SpecFolderCollector TERRA_FUNCTIONAL_TESTING_SPEC_COLLECTOR =
        new SpecFolderCollector(dir -> dir.getParent().getParent().getParent().getParent().getName());
    private ScreenshotContextParser contextParserWithSeparator;

    //Required for project service creation
    public TerraFunctionalTestingManager(Project project) {
    }

    @Override
    public ScreenshotNameResolver screenshotNameResolver() {
        return TerraFunctionalTestingScreenshotNameResolver.INSTANCE;
    }

    @Override
    public ScreenshotContextParser screenshotContextParser() {
        return TerraFunctionalTestingScreenshotContextParser.INSTANCE;
    }

    @Override
    public ScreenshotContextParser screenshotContextParser(String contextSeparator) {
        return Optional.ofNullable(contextParserWithSeparator)
            .orElseGet(() -> contextParserWithSeparator = new TerraFunctionalTestingScreenshotContextParser(contextSeparator));
    }

    @Override
    public SpecFolderCollector specFolderCollector() {
        return TERRA_FUNCTIONAL_TESTING_SPEC_COLLECTOR;
    }

    @Override
    public TerraPropertiesProvider screenshotValidationProperties() {
        return TerraFunctionalTestingPropertiesProvider.INSTANCE;
    }
}
