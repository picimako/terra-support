//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import java.util.Collections;
import java.util.List;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.screenshot.ScreenshotNameResolver;
import com.picimako.terra.wdio.screenshot.inspection.TerraPropertiesProvider;

/**
 * No-operation resource manager to use when there is no terra-toolkit or terra-functional-testing dependency in the
 * project's root package.json, or there is no root package.json at all.
 */
@Service //Service.Level.PROJECT
public final class NoopResourceManager extends TerraResourceManager {

    //Required for project service creation
    public NoopResourceManager(Project project) {
    }

    @Override
    public ScreenshotNameResolver screenshotNameResolver() {
        return new ScreenshotNameResolver() {
            @Override
            public @Nullable String resolveName(JSLiteralExpression element) {
                return null;
            }

            @Override
            public @Nullable String resolveDefaultName(JSExpression methodExpression) {
                return null;
            }

            @Override
            public @Nullable String resolveWithFallback(@Nullable JSLiteralExpression firstNameArgument, JSExpression methodExpression) {
                return null;
            }
        };
    }

    @Override
    public ScreenshotContextParser screenshotContextParser() {
        return new ScreenshotContextParser() {
            @Override
            public String parse(@NotNull String filePath) {
                return null;
            }
        };
    }

    @Override
    public ScreenshotContextParser screenshotContextParser(String contextSeparator) {
        return screenshotContextParser();
    }

    @Override
    public SpecFolderCollector specFolderCollector() {
        return dir -> null;
    }

    @Override
    public TerraPropertiesProvider screenshotValidationProperties() {
        return new TerraPropertiesProvider() {
            @Override
            public List<String> screenshotProperties() {
                return Collections.emptyList();
            }

            @Override
            public List<String> elementProperties() {
                return Collections.emptyList();
            }

            @Override
            public List<String> accessibilityProperties() {
                return Collections.emptyList();
            }
        };
    }
}
