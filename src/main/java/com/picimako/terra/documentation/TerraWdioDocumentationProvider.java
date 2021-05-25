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

package com.picimako.terra.documentation;

import static com.intellij.lang.documentation.DocumentationMarkup.CONTENT_END;
import static com.intellij.lang.documentation.DocumentationMarkup.CONTENT_START;
import static com.picimako.terra.FileTypePreconditions.isInWdioSpecFile;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isAnyOfTerraWdioFunctions;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;

import com.intellij.lang.javascript.documentation.JavaScriptDocumentationProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraResourceManager;

/**
 * Provides external documentation links for Terra wdio objects and functions in Terra wdio spec files.
 * <p>
 * The links are added in addition to the original documentation content what their Quick Documentation popup provides,
 * which could be achieved by having this class extended from {@link JavaScriptDocumentationProvider}.
 *
 * @see TerraQuickDocBundle
 * @since 0.1.0
 */
public class TerraWdioDocumentationProvider extends JavaScriptDocumentationProvider {

    private static final String HTML_LINK = "<a href=\"%s\">%s</a>";

    /**
     * {@inheritDoc}
     * <p>
     * Generates the documentation only when the element on which the Quick Documentation is invoked is a Terra function or object,
     * and it is within a Terra wdio spec file.
     */
    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (originalElement != null && isInWdioSpecFile(originalElement) && isAnyOfTerraWdioFunctions(originalElement)) {
            return super.generateDoc(element, originalElement) + generateDocLinks(originalElement);
        }
        return null;
    }

    /**
     * Generates the documentation HTML snippet for the popup.
     * <p>
     * Each entry in the source properties file may have one or more links separated by commas, and the text and URL
     * parts of each links separated by pipe (|) characters.
     * <p>
     * Extra trimming and blank/empty string checking is not included here, since the source content is not dynamic.
     * <p>
     * When adding entries into {@code TerraQuickDocBundle.properties}, please make sure that they are added as non-blank,
     * trimmed values.
     *
     * @param originalElement the element to show the documentation popup for
     * @return the generated HTML code to render in the documentation popup
     */
    @NotNull
    private String generateDocLinks(PsiElement originalElement) {
        String[] links = TerraQuickDocBundle.message(libraryPrefix(originalElement.getProject()) + originalElement.getParent().getText()).split(",");
        return CONTENT_START + Arrays.stream(links)
            .map(link -> String.format(HTML_LINK, TerraQuickDocBundle.message(link + ".url"), TerraQuickDocBundle.message(link + ".label")))
            .collect(joining("<br>")) + CONTENT_END;
    }

    private String libraryPrefix(Project project) {
        return TerraResourceManager.isUsingTerraToolkit(project) ? "tt." : "tft.";
    }
}
