/*
 * Copyright 2020 Tamás Balog
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

package com.picimako.terra.wdio.screenshot;

import java.util.function.Supplier;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.screenshot.reference.TerraScreenshotNameResolver;

/**
 * Collects screenshots based on the text of literal expressions.
 */
public class TerraScreenshotCollector {

    private final TerraScreenshotNameResolver screenshotNameResolver = new TerraScreenshotNameResolver();

    /**
     * Collects screenshots from the current project based on the provided JS literal expression.
     *
     * @param element the literal expression based on whose text the search is performed
     * @return the array of screenshot files found
     */
    @NotNull
    public PsiFile[] collectFor(JSLiteralExpression element) {
        return collect(element.getProject(), () -> screenshotNameResolver.resolveName(element));
    }

    @NotNull
    public PsiFile[] collectForDefault(JSExpression methodExpression) {
        return collect(methodExpression.getProject(), () -> screenshotNameResolver.resolveDefaultName(methodExpression));
    }

    private PsiFile[] collect(Project project, Supplier<String> nameSupplier) {
        return FilenameIndex.getFilesByName(project, nameSupplier.get(), GlobalSearchScope.projectScope(project));
    }
}
