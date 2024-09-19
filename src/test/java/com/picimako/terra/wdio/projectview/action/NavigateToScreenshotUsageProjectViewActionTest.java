//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.projectview.action;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.TestActionEvent;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link NavigateToScreenshotUsageProjectViewAction}.
 */
public class NavigateToScreenshotUsageProjectViewActionTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testNavigatesToDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        action.actionPerformed(doTestActionEvent(screenshot));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(277);
    }

    public void testNavigatesToNonDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[non-default].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        action.actionPerformed(doTestActionEvent(screenshot));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(401);
    }

    public void testShowDialogOfNoValidationLinkedToTheScreenshot() {
        myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> action.actionPerformed(doTestActionEvent(screenshot)))
            .withMessage("There is no validation linked to this screenshot.\n" +
                "Either it has been removed entirely or is now referencing this image by a different name.");
    }

    public void testNoNavigationHappensForNoSelectedScreenshot() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        action.actionPerformed(TestActionEvent.createTestEvent(dataId -> CommonDataKeys.PROJECT.is(dataId) ? getProject() : null));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isZero();
    }

    public void testShowNoSpecFileToNavigateToDialog() {
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();

        assertThat(FileEditorManager.getInstance(getProject()).hasOpenFiles()).isFalse();
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> action.actionPerformed(doTestActionEvent(screenshot)))
            .withMessage("There is no spec file available to navigate to. It may have been removed.");
    }

    private AnActionEvent doTestActionEvent(PsiFile screenshot) {
        return TestActionEvent.createTestEvent(dataId -> {
            if (CommonDataKeys.PSI_FILE.is(dataId)) return screenshot;
            if (CommonDataKeys.PROJECT.is(dataId)) return getProject();
            return null;
        });
    }
}
