//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getCallExpression;
import static com.picimako.terra.psi.js.JSArgumentUtil.getArgumentsOf;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.isJSStringLiteral;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.psi.js.JSLiteralExpressionUtil;

/**
 * Provides utility methods and properties for Terra WDIO test related features.
 */
public final class TerraWdioPsiUtil {

    //Validation calls
    public static final String TERRA_VALIDATES_SCREENSHOT = "Terra.validates.screenshot";
    public static final String TERRA_VALIDATES_ELEMENT = "Terra.validates.element";
    public static final String TERRA_IT_MATCHES_SCREENSHOT = "Terra.it.matchesScreenshot";
    public static final String TERRA_IT_VALIDATES_ELEMENT = "Terra.it.validatesElement";
    public static final String TERRA_IT_IS_ACCESSIBLE = "Terra.it.isAccessible";
    public static final String TERRA_VALIDATES_ACCESSIBILITY = "Terra.validates.accessibility";

    //Viewports
    public static final String TERRA_DESCRIBE_VIEWPORTS = "Terra.describeViewports";
    public static final String TERRA_DESCRIBE_TESTS = "Terra.describeTests";
    public static final String TERRA_VIEWPORTS = "Terra.viewports";

    //Terra.describeTests properties
    public static final String FORM_FACTORS = "formFactors";

    //Other utilities
    public static final String TERRA_HIDE_INPUT_CARET = "Terra.hideInputCaret";

    //Partial Terra names
    public static final String TERRA = "Terra";
    public static final String TERRA_VALIDATES = "Terra.validates";
    public static final String TERRA_IT = "Terra.it";

    //Validation properties
    //terra-functional-testing version
    public static final String MISMATCH_TOLERANCE = "mismatchTolerance";
    public static final String RULES = "rules";
    //terra-toolkit version
    public static final String MIS_MATCH_TOLERANCE = "misMatchTolerance";
    public static final String SELECTOR = "selector";
    public static final String VIEWPORTS = "viewports";
    public static final String AXE_RULES = "axeRules";

    //Spec files
    public static final String WDIO_SPEC_FILE_NAME_PATTERN = ".*-spec\\.(jsx?|ts)$";

    private static final Set<String> TERRA_FUNCTIONS = Set.of(
        TERRA_DESCRIBE_VIEWPORTS, TERRA_VIEWPORTS, TERRA_HIDE_INPUT_CARET,
        TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT, TERRA_IT_IS_ACCESSIBLE,
        TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT, TERRA_VALIDATES_ACCESSIBILITY,
        TERRA_VALIDATES, TERRA_IT);

    /**
     * The items in the list are in ascending order by their widths.
     */
    public static final List<String> SUPPORTED_TERRA_VIEWPORTS = List.of("tiny", "small", "medium", "large", "huge", "enormous");

    public static final Set<String> SCREENSHOT_VALIDATION_NAMES = Set.of(
        TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT, TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT);


    /**
     * Gets whether the viewport literal String in the argument is supported by terra.
     *
     * @param viewport the JSExpression Psi node of the viewport
     * @return true if the viewport is supported by terra, false otherwise
     */
    public static boolean isSupportedViewport(JSExpression viewport) {
        return isJSStringLiteral(viewport) && isSupportedViewport(getStringValue(viewport));
    }

    /**
     * Gets whether the argument viewport is supported by terra.
     *
     * @param viewport the viewport to check
     * @return true if the viewport is supported by terra, false otherwise
     */
    public static boolean isSupportedViewport(@Nullable String viewport) {
        return viewport != null && SUPPORTED_TERRA_VIEWPORTS.contains(viewport);
    }

    /**
     * Retrieves the viewports values from the argument {@code Terra.describeViewports} block.
     *
     * @param describeViewports the element representing the describeViewports block
     * @return the viewport expression as an array
     * @since 0.4.0
     */
    @NotNull
    public static JSExpression[] getViewports(JSExpressionStatement describeViewports) {
        var terraDescribeViewports = getCallExpression(describeViewports);
        if (terraDescribeViewports != null) {
            var argumentList = terraDescribeViewports.getArgumentList();
            if (argumentList != null && argumentList.getArguments().length > 1) {
                var viewportList = argumentList.getArguments()[1];
                if (viewportList instanceof JSArrayLiteralExpression) {
                    return ((JSArrayLiteralExpression) viewportList).getExpressions();
                }
            }
        }
        return JSExpression.EMPTY_ARRAY;
    }

    /**
     * Retrieves the viewports values from the argument {@code Terra.describeViewports} block as Strings.
     *
     * @param describeViewports the element representing the describeViewports block
     * @return the set of viewport String values
     * @since 0.4.0
     */
    @NotNull
    public static Set<String> getViewportsSet(PsiElement describeViewports) {
        return Arrays.stream(getViewports((JSExpressionStatement) describeViewports))
            .map(JSLiteralExpressionUtil::getStringValue)
            .filter(Objects::nonNull)
            .collect(toSet());
    }

    /**
     * Retrieves the viewports values from the provided viewport element collection as Strings.
     *
     * @param viewports the elements representing the viewport literals in the describeViewports block
     * @return the list of viewport String values
     * @since 0.7.0
     */
    @NotNull
    public static List<String> getViewports(JSExpression[] viewports) {
        return Arrays.stream(viewports).map(JSLiteralExpressionUtil::getStringValue).filter(Objects::nonNull).toList();
    }

    /**
     * Gets the underlying method expression of the argument element.
     * <p>
     * From that expression further information about the function call identifier can be retrieved.
     *
     * @param element the element to query
     * @return the underlying method expression if found, null otherwise
     */
    @Nullable
    public static JSExpression getMethodExpressionOf(@NotNull PsiElement element) {
        var jsCallExpression = getCallExpression(element);
        return jsCallExpression != null ? jsCallExpression.getMethodExpression() : null;
    }

    /**
     * Gets whether the argument element is one of the Terra wdio objects or functions.
     *
     * @param element the element to check
     * @return true if the element is one of the Terra wdio objects or functions, false otherwise
     */
    public static boolean isAnyOfTerraWdioFunctions(@NotNull PsiElement element) {
        return element.getParent() != null && TERRA_FUNCTIONS.contains(element.getParent().getText());
    }

    /**
     * Gets whether the element's underlying method expression has any of the text provided in the {@code desiredTexts} argument.
     *
     * @param element      the element to check the text of
     * @param desiredTexts the acceptable collection of texts
     * @return true if there is at least one desired text, and the element has text of any of the provided texts, false otherwise
     */
    public static boolean hasText(@NotNull PsiElement element, String... desiredTexts) {
        if (desiredTexts.length > 0) {
            var methodExpression = getMethodExpressionOf(element);
            return methodExpression != null && Arrays.stream(desiredTexts).anyMatch(methodExpression::textMatches);
        }
        return false;
    }

    /**
     * Gets whether the argument expression is a screenshot validation call.
     *
     * @param expression the expression to validate
     * @return true is the expression is a screenshot validation, false otherwise
     * @see #SCREENSHOT_VALIDATION_NAMES
     */
    public static boolean isScreenshotValidationCall(@Nullable JSCallExpression expression) {
        if (expression != null) {
            var methodExpression = expression.getMethodExpression();
            return methodExpression != null && SCREENSHOT_VALIDATION_NAMES.contains(methodExpression.getText());
        }
        return false;
    }

    /**
     * Returns whether the argument element is a top level expression.
     * <p>
     * This is achieved by checking whether the element's parent is the containing file itself.
     *
     * @param element the element to check
     * @since 0.6.0
     */
    public static boolean isTopLevelExpression(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiFile;
    }

    public static boolean isTerraIt(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT, TERRA_IT_IS_ACCESSIBLE);
    }

    /**
     * Gets whether the argument element is a {@code Terra.it.matchesScreenshot()} call at any level deep.
     * <p>
     * Such calls at top level are ignored.
     *
     * @param element the Psi element to check
     * @return true if the argument is a non-top-level {@code Terra.it.matchesScreenshot()}, false otherwise
     */
    public static boolean isTerraItMatchesScreenshot(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_IT_MATCHES_SCREENSHOT);
    }

    /**
     * Gets whether the argument element corresponds to one of the Terra function calls that do screenshot validation,
     * namely: {@code Terra.it.matchesScreenshot}, {@code Terra.it.validatesElement}, {@code Terra.validates.screenshot}
     * and {@code Terra.validates.element}.
     *
     * @param element the element to validate
     * @return true if the argument is one of the Terra screenshot matching functions, false otherwise
     */
    public static boolean isTerraElementOrScreenshotValidation(PsiElement element) {
        return !isTopLevelExpression(element)
            && hasText(element, TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT, TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT);
    }

    public static boolean isNonTerraItElementOrScreenshotValidation(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT);
    }

    public static boolean isScreenshotValidation(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_IT_MATCHES_SCREENSHOT, TERRA_VALIDATES_SCREENSHOT);
    }

    public static boolean isElementValidation(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_IT_VALIDATES_ELEMENT, TERRA_VALIDATES_ELEMENT);
    }

    public static boolean isAccessibilityValidation(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_IT_IS_ACCESSIBLE, TERRA_VALIDATES_ACCESSIBILITY);
    }

    public static boolean isInContextOfNonTerraItElementOrScreenshotValidation(JSLiteralExpression literal) {
        return Optional.of(literal)
            .map(PsiElement::getParent)
            .map(PsiElement::getPrevSibling)
            .map(methodExpression -> Stream.of(TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT).anyMatch(methodExpression::textMatches))
            .orElse(false);
    }

    /**
     * Returns the JS property for the argument property name within the provided PsiElement,
     * or null if the element has no such property.
     * <p>
     * For example, in case of:
     * <pre>
     * Terra.validates.element({ misMatchTolerance: 0.7 });
     * </pre>
     * it will return the element corresponding to the "{@code misMatchTolerance: 0.7}" part.
     *
     * @param element              the Psi element to get the property from
     * @param propertyNameVariants variants of a property name to retrieve any of them if found
     * @return the JSProperty for the property, or null
     */
    @Nullable
    public static JSProperty getScreenshotValidationProperty(PsiElement element, String... propertyNameVariants) {
        var propertiesObject = getTerraValidationPropertyObject(element);
        return propertiesObject != null
            ? Arrays.stream(propertyNameVariants)
            .map(propertiesObject::findProperty)
            .filter(Objects::nonNull)
            .findAny()
            .orElse(null)
            : null;
    }

    private static JSObjectLiteralExpression getTerraValidationPropertyObject(PsiElement element) {
        JSExpression[] arguments = null;
        if (element instanceof JSExpressionStatement) {
            arguments = getArgumentsOf((JSExpressionStatement) element);
        } else if (element instanceof JSCallExpression) {
            var argumentList = ((JSCallExpression) element).getArgumentList();
            if (argumentList != null) {
                arguments = argumentList.getArguments();
            }
        }
        if (arguments != null && arguments.length > 0 && arguments.length < 3 && arguments[arguments.length - 1] instanceof JSObjectLiteralExpression) {
            return (JSObjectLiteralExpression) arguments[arguments.length - 1];
        }

        return null;
    }

    /**
     * Returns the list of JS properties from Terra.it and Terra.validates helpers.
     *
     * @param element the JSExpressionStatement to get the properties from
     * @since 0.6.0
     */
    public static JSProperty[] getTerraValidationProperties(PsiElement element) {
        return Optional.ofNullable(getTerraValidationPropertyObject(element))
            .map(JSObjectLiteralExpression::getProperties)
            .orElse(JSProperty.EMPTY_ARRAY);
    }

    private TerraWdioPsiUtil() {
        //Utility class
    }
}
