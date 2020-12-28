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

package com.picimako.terra;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.psi.PsiElement;
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
    private static final String SPEC_FILE_POSTFIX = "-spec.js";

    /**
     * Validates whether the argument element is in a non-test React JSX file, as in its name ends with {@code .jsx} but
     * not with {@code .test.jsx}.
     *
     * @param element the Psi element to check
     * @return true if the element is a React jsx file, false otherwise
     */
    public static boolean isInAppJsxFile(@NotNull PsiElement element) {
        return element.getContainingFile().getName().matches(NON_TEST_JSX_PATTERN);
    }

    /**
     * Validates whether the argument element is in a wdio spec file, as in its name ends with {@code -spec.js}.
     *
     * @param element the Psi element to check
     * @return true if the element is a wdio spec file, false otherwise
     */
    public static boolean isInWdioSpecFile(@NotNull PsiElement element) {
        return element.getContainingFile().getName().endsWith(SPEC_FILE_POSTFIX);
    }

    private FileTypePreconditions() {
        //Utility class
    }
}
