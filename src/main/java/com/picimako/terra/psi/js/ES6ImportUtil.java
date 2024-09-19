//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.psi.js;

import java.util.Arrays;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.lang.ecmascript6.psi.ES6FromClause;
import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration;
import com.intellij.lang.ecmascript6.psi.ES6ImportSpecifier;
import com.intellij.lang.ecmascript6.psi.ES6ImportedBinding;
import com.intellij.lang.ecmascript6.psi.ES6NamedImports;
import com.intellij.lang.ecmascript6.psi.impl.ES6ImportPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides utility methods to retrieve various information about import statements from JavaScript files.
 */
public final class ES6ImportUtil {

    /**
     * Returns the import path from which the argument element is imported from, or null if no path is defined.
     *
     * @param element the element to get the import path of
     * @return the import path, or null if it is not defined
     */
    @Nullable
    public static String importPathForBaseComponent(@NotNull PsiElement element) {
        final ES6ImportDeclaration importByName = findImportByBaseName(element);
        return importByName != null ? importPathFor(importByName) : null;
    }

    /**
     * Returns the import path from the argument import declaration.
     * <p>
     * It supports all import variations described by the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import">Mozilla MDN import article</a>.
     * <p>
     * If there is no import path at all, this method returns null.
     *
     * @param declaration the import declaration to get the path of
     * @return the import path if exists, otherwise null
     */
    @Nullable
    public static String importPathFor(ES6ImportDeclaration declaration) {
        final ES6FromClause fromClause = declaration.getFromClause();
        String importPath = null;
        if (fromClause != null) {
            importPath = fromClause.getReferenceText();
        } else if (declaration.getImportModuleText() != null) { //Import a module for its side effects only
            importPath = declaration.getImportModuleText();
        }
        return importPath != null ? importPath.substring(1, importPath.length() - 1) : null; //strip start and end ' and " characters
    }

    /**
     * Finds the import declaration for the argument Psi element, or returns null if there is no import for that.
     *
     * @param element the Psi element to find the import of
     * @return the {@link ES6ImportDeclaration} instance for the Psi element, or null if there is none
     */
    @Nullable
    public static ES6ImportDeclaration findImportByBaseName(@NotNull PsiElement element) {
        final String baseComponentName = getBaseComponentName(element);
        return ES6ImportPsiUtil.getImportDeclarations(element.getContainingFile())
            .stream()
            .filter(declaration -> isImportDeclarationForElementBase(declaration, baseComponentName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks whether the argument import {@code declaration} is the import for the argument {@code element}.
     * <p>
     * For example if the inputs are
     * <pre>
     * declaration: import ResponsiveElement from 'terra-responsive-element';
     * element:     &lt;ResponsiveElement ...>
     * </pre>
     * then this method will return true. But it will return false if the inputs doesn't match, like in the example below:
     * <pre>
     * declaration: import ContentContainer from 'terra-content-container';
     * element:     &lt;ResponsiveElement ...>
     * </pre>
     * <b>PSI information:</b>
     * <p>
     * In case of
     * <pre>
     * import ResponsiveElement from 'terra-responsive-element';
     * </pre>
     * type imports, there is a single {@link ES6ImportedBinding} returned which contains {@code ResponsiveElement}.
     * <p>
     * In case of
     * <pre>
     * import { DisclosureManagerContext, DisclosureManagerHeaderAdapter } from 'terra-disclosure-manager';
     * </pre>
     * type imports, there is no ES6ImportedBinding returned, rather the {@code { DisclosureManagerContext, DisclosureManagerHeaderAdapter }} part
     * is an {@link ES6NamedImports} object containing multiple (2 in this case) {@link ES6ImportSpecifier}s.
     * <p>
     * In case the imported object has subtypes, e.g. the {@code Grid} Terra component has {@code Grid.Row} and {@code Grid.Column},
     * then the search will happen only for {@code Grid}.
     *
     * @param baseComponentName the base component name
     * @param declaration       the import declaration which is checked whether it is for the argument element
     * @return true if the declaration is for the provided element, false otherwise
     */
    public static boolean isImportDeclarationForElementBase(ES6ImportDeclaration declaration, String baseComponentName) {
        boolean isImport;
        final ES6ImportedBinding[] importedBindings = declaration.getImportedBindings();
        isImport = isMatchingImport(importedBindings, baseComponentName);
        if (!isImport) {
            final ES6NamedImports namedImports = declaration.getNamedImports();
            if (namedImports != null) {
                isImport = isMatchingImport(namedImports.getSpecifiers(), baseComponentName);
            }
        }
        return isImport;
    }

    private static boolean isMatchingImport(PsiNamedElement[] imports, String baseComponentName) {
        if (imports.length > 0) {
            return imports.length == 1
                ? baseComponentName.equals(imports[0].getName())
                : Arrays.stream(imports).anyMatch(specifier -> baseComponentName.equals(specifier.getName()));
        }
        return false;
    }

    /**
     * Gets the base component name from the argument element.
     * <p>
     * In case the component has subtype(s), e.g. the {@code Grid} Terra component has {@code Grid.Row} and {@code Grid.Column},
     * then it returns {@code Grid}.
     * <p>
     * If the component doesn't have a subtype, this method returns the component name.
     *
     * @param element the element to get the component name of
     * @return the base component name
     */
    @NotNull
    @VisibleForTesting
    static String getBaseComponentName(@NotNull PsiElement element) {
        String componentName = element.getText();
        if (componentName.contains(".")) {
            componentName = componentName.substring(0, componentName.indexOf("."));
        }
        return componentName;
    }

    private ES6ImportUtil() {
        //Utility class
    }
}
