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
 * No-operation resource manager to use when there is no terra-toolkit or terra-functional-testing depencency in the
 * project's root package.json, or there is no root package.json at all.
 */
@Service
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