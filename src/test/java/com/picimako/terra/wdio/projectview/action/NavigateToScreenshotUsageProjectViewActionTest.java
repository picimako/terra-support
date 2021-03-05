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

package com.picimako.terra.wdio.projectview.action;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.TestActionEvent;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link NavigateToScreenshotUsageProjectViewAction}.
 */
public class NavigateToScreenshotUsageProjectViewActionTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testNavigatesToDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        action.actionPerformed(testActionEvent(screenshot));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(741);
    }

    public void testNavigatesToNonDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[non-default].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        action.actionPerformed(testActionEvent(screenshot));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(865);
    }

    public void testShowDialogOfNoValidationLinkedToTheScreenshot() {
        myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> action.actionPerformed(testActionEvent(screenshot)))
            .withMessage("There is no validation linked to this screenshot.\n" +
                "Either it has been removed entirely or is now referencing this image by a different name.");
    }

    public void testNoNavigationHappensForNoSelectedScreenshot() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();
        action.actionPerformed(new TestActionEvent(dataId -> CommonDataKeys.PROJECT.is(dataId) ? getProject() : null));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(0);
    }

    public void testShowNoSpecFileToNavigateToDialog() {
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png")));

        NavigateToScreenshotUsageProjectViewAction action = new NavigateToScreenshotUsageProjectViewAction();

        assertThat(FileEditorManager.getInstance(getProject()).hasOpenFiles()).isFalse();
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> action.actionPerformed(testActionEvent(screenshot)))
            .withMessage("There is no spec file available to navigate to. It may have been removed.");
    }

    private TestActionEvent testActionEvent(PsiFile screenshot) {
        return new TestActionEvent(dataId -> {
            if (PlatformDataKeys.PSI_FILE.is(dataId)) {
                return screenshot;
            }
            if (CommonDataKeys.PROJECT.is(dataId)) {
                return getProject();
            }
            return null;
        });
    }
}
