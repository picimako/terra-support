//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link ToScreenshotUsageNavigator}.
 */
public class ToScreenshotUsageNavigatorTest extends TerraToolkitTestCase {

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
        assertThat(myFixture.getCaretOffset()).isZero();
    }
}
