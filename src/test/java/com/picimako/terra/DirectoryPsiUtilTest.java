//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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

    public void testNotReturnDirectoryWhenDirectoryPathIsNull() {
        myFixture.configureByFile("tests/wdio/MissingScreenshots-spec.js");
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), null);

        assertThat(tests).isNull();
    }

    public void testNotReturnDirectorySubDirectoryIsNull() {
        myFixture.configureByFile("tests/wdio/MissingScreenshots-spec.js");
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), "tests/jest");

        assertThat(tests).isNull();
    }

    public void testReturnDirectoryForName() {
        myFixture.configureByFile("tests/wdio/MissingScreenshots-spec.js");
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), "tests");

        assertThat(tests).isNotNull();
        assertThat(tests.getVirtualFile().getPath()).endsWith("/src/tests");
    }

    public void testReturnDirectoryForPath() {
        myFixture.configureByFile("tests/wdio/MissingScreenshots-spec.js");
        PsiDirectory tests = DirectoryPsiUtil.findDirectory(getProject(), "tests/wdio");

        assertThat(tests).isNotNull();
        assertThat(tests.getVirtualFile().getPath()).endsWith("/src/tests/wdio");
    }
}
