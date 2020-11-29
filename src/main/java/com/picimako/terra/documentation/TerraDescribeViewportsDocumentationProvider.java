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
import static com.intellij.lang.documentation.DocumentationMarkup.DEFINITION_END;
import static com.intellij.lang.documentation.DocumentationMarkup.DEFINITION_START;
import static com.intellij.lang.documentation.DocumentationMarkup.SECTIONS_END;
import static com.intellij.lang.documentation.DocumentationMarkup.SECTIONS_START;
import static com.intellij.lang.documentation.DocumentationMarkup.SECTION_END;
import static com.intellij.lang.documentation.DocumentationMarkup.SECTION_HEADER_START;
import static com.intellij.lang.documentation.DocumentationMarkup.SECTION_SEPARATOR;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.isJSStringLiteral;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isSupportedViewport;

import java.util.Map;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Generates Quick Documentation content for viewport values specified in {@code Terra.describeViewports} arguments.
 */
public final class TerraDescribeViewportsDocumentationProvider implements DocumentationProvider {

    /**
     * Documentation snippets based on the
     * <a href="https://engineering.cerner.com/terra-ui/components/terra-breakpoints/breakpoints/about#breakpoints">Terra Breakpoints documentation</a>.
     */
    private static final Map<String, Breakpoint> BREAKPOINTS = Map.of(
            "tiny", new Breakpoint("0px", "@media screen and (min-width: 0px)", "Active from viewport width 0px and up"),
            "small", new Breakpoint("544px", "@media screen and (min-width: 544px)", "Active from viewport width 544px and up"),
            "medium", new Breakpoint("768px", "@media screen and (min-width: 768px)", "Active from viewport width 768px and up"),
            "large", new Breakpoint("992px", "@media screen and (min-width: 992px)", "Active from viewport width 992px and up"),
            "huge", new Breakpoint("1216px", "@media screen and (min-width: 1216px)", "Active from viewport width 1216px and up"),
            "enormous", new Breakpoint("1440px", "@media screen and (min-width: 1440px)", "Active from viewport width 1440px and up"));

    /**
     * Actual documentation is built only for those String literals that are defined in the second argument of {@code Terra.describeViewports}
     * functions (as in defined in an array literal within an argument list), and are valid viewport values supported by the terra library.
     */
    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (isJSStringLiteral(element)
                && element.getParent() instanceof JSArrayLiteralExpression
                && element.getParent().getParent() instanceof JSArgumentList
                && element.getParent().getParent().getPrevSibling() instanceof JSReferenceExpression
                && TERRA_DESCRIBE_VIEWPORTS.equals(((JSReferenceExpression) element.getParent().getParent().getPrevSibling()).getCanonicalText())
                && isSupportedViewport(getStringValue(element))) {
            return buildDocumentation(getStringValue(element));
        }
        return null;
    }

    private String buildDocumentation(String viewport) {
        return DEFINITION_START + viewport + DEFINITION_END
                + CONTENT_START
                + SECTIONS_START
                + SECTION_HEADER_START + "Minimum value: " + SECTION_SEPARATOR + BREAKPOINTS.get(viewport).minimumValue + SECTION_SEPARATOR + SECTION_END
                + SECTION_HEADER_START + "Media Query: " + SECTION_SEPARATOR + "<code>" + BREAKPOINTS.get(viewport).mediaQuery + "</code>" + SECTION_END
                + SECTION_HEADER_START + "Description: " + SECTION_SEPARATOR + BREAKPOINTS.get(viewport).description + SECTION_END
                + SECTIONS_END
                + CONTENT_END;
    }

    private static final class Breakpoint {
        private final String minimumValue;
        private final String mediaQuery;
        private final String description;

        Breakpoint(String minimumValue, String mediaQuery, String description) {
            this.minimumValue = minimumValue;
            this.mediaQuery = mediaQuery;
            this.description = description;
        }
    }
}
