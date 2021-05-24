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

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link ToScreenshotUsageNavigator}.
 */
public class ToScreenshotUsageNavigatorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testNavigatesToDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png"));

        ToScreenshotUsageNavigator navigator = new ToScreenshotUsageNavigator(getProject());
        navigator.navigateToUsage(specFile, "terra_screenshot[default].png");

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(741);
    }

    public void testNavigatesToNonDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[non-default].png"));

        ToScreenshotUsageNavigator navigator = new ToScreenshotUsageNavigator(getProject());
        navigator.navigateToUsage(specFile, "terra_screenshot[non-default].png");

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(865);
    }

    public void testDoesntNavigateToNotReferencedImage() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png"));

        ToScreenshotUsageNavigator navigator = new ToScreenshotUsageNavigator(getProject());
        navigator.navigateToUsage(specFile, "terra_screenshot[non-referenced].png");

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(0);
    }
}
