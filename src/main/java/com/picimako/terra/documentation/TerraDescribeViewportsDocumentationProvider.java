//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.isJSStringLiteral;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_TESTS;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isSupportedViewport;

import java.util.Map;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Generates Quick Documentation content for viewport values specified in {@code Terra.describeViewports} arguments.
 *
 * @since 0.1.0
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
        if (element != null
            && isUsingTerra(element.getProject())
            && isWdioSpecFile(element.getContainingFile())
            && isJSStringLiteral(element)
            && element.getParent() instanceof JSArrayLiteralExpression
            && (isViewportInDescribeViewports(element) || isViewportInDescribeTests(element))
            && isSupportedViewport(getStringValue(element))) {
            return buildDocumentation(getStringValue(element));
        }
        return null;
    }

    private boolean isViewportInDescribeViewports(PsiElement element) {
        return element.getParent().getParent() instanceof JSArgumentList
            && element.getParent().getParent().getPrevSibling() instanceof JSReferenceExpression
            && TERRA_DESCRIBE_VIEWPORTS.equals(((JSReferenceExpression) element.getParent().getParent().getPrevSibling()).getCanonicalText());
    }

    private boolean isViewportInDescribeTests(PsiElement element) {
        return element.getParent().getParent() instanceof JSProperty
            && element.getParent().getParent().getParent() instanceof JSObjectLiteralExpression
            && element.getParent().getParent().getParent().getParent() instanceof JSArgumentList
            && element.getParent().getParent().getParent().getParent().getPrevSibling() instanceof JSReferenceExpression
            && TERRA_DESCRIBE_TESTS.equals(((JSReferenceExpression) element.getParent().getParent().getParent().getParent().getPrevSibling()).getCanonicalText());
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

    private record Breakpoint(String minimumValue, String mediaQuery, String description) {
    }
}
