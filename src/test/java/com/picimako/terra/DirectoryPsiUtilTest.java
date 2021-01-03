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

package com.picimako.terra;


import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiDirectory;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link DirectoryPsiUtil}.
 */
public class DirectoryPsiUtilTest extends BasePlatformTestCase{

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFile("tests/wdio/MissingScreenshots-spec.js");
    }

    public void testNotReturnDirectoryWhenDirectoryPathIsNull() {
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), null);

        assertThat(tests).isNull();
    }

    public void testNotReturnDirectorySubDirectoryIsNull() {
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), "tests/jest");

        assertThat(tests).isNull();
    }

    public void testReturnDirectoryForName() {
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), "tests");

        assertThat(tests).isNotNull();
        assertThat(tests.getVirtualFile().getPath()).endsWith("/src/tests");
    }

    public void testReturnDirectoryForPath() {
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), "tests/wdio");

        assertThat(tests).isNotNull();
        assertThat(tests.getVirtualFile().getPath()).endsWith("/src/tests/wdio");
    }
}
