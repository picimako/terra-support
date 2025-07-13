//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.WDIO_SPEC_FILE_NAME_PATTERN;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods for validating file types as a precondition for running inspections.
 * <p>
 * Calling these methods must be the first check point in inspections to ensure that when the IntelliJ
 * platform runs the inspection on other file types, it won't fail with e.g. a {@link ClassCastException}.
 * <p>
 * This a kind of workaround because the IntelliJ platform, specifically neither the {@code localInspection} extension point
 * nor {@link LocalInspectionTool} don't provide a way to filter the execution of inspection classes by file name
 * or pattern.
 */
public final class FileTypePreconditions {

    private static final String NON_TEST_JSX_PATTERN = ".*(?<!\\.test)\\.jsx$";

    /**
     * Returns whether the argument element is in a non-test React JSX file, as in its name ends with {@code .jsx} but
     * not with {@code .test.jsx}.
     *
     * @param element the Psi element to check
     * @return true if the element is a React jsx file, false otherwise
     */
    public static boolean isInAppJsxFile(@NotNull PsiElement element) {
        return element.getContainingFile().getName().matches(NON_TEST_JSX_PATTERN);
    }

    /**
     * Returns whether the argument element is in a wdio spec file, as in its name ends with one of the following:
     * -spec.js, -spec.jsx or -spec.ts.
     *
     * @param element the Psi element to check
     * @return true if the element is a wdio spec file, false otherwise
     */
    public static boolean isInWdioSpecFile(@NotNull PsiElement element) {
        return element.getContainingFile().getName().matches(WDIO_SPEC_FILE_NAME_PATTERN);
    }

    /**
     * Returns whether the argument file is a wdio spec file, as in its name ends with one of the following:
     * -spec.js, -spec.jsx or -spec.ts.
     *
     * @param file the Psi file to check
     * @return true if it is a wdio spec file, false otherwise
     */
    public static boolean isWdioSpecFile(PsiFile file) {
        return file.getName().matches(WDIO_SPEC_FILE_NAME_PATTERN);
    }

    private FileTypePreconditions() {
        //Utility class
    }
}
