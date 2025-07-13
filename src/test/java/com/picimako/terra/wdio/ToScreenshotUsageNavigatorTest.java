//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.fileEditor.FileEditorManager;

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
        var specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png"));

        var navigator = new ToScreenshotUsageNavigator(getProject());
        navigator.navigateToUsage(specFile, "terra_screenshot[default].png");

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(277);
    }

    public void testNavigatesToNonDefaultScreenshotValidation() {
        var specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[non-default].png"));

        var navigator = new ToScreenshotUsageNavigator(getProject());
        navigator.navigateToUsage(specFile, "terra_screenshot[non-default].png");

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(401);
    }

    public void testDoesntNavigateToNotReferencedImage() {
        var specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png"));

        var navigator = new ToScreenshotUsageNavigator(getProject());
        navigator.navigateToUsage(specFile, "terra_screenshot[non-referenced].png");

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isZero();
    }
}
