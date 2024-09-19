//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import javax.swing.tree.TreePath;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.TestActionEvent;

import com.picimako.terra.TerraToolkitTestCase;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeModel;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Unit test for {@link NavigateToScreenshotUsageAction}.
 */
public class NavigateToScreenshotUsageActionTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testNavigatesToDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png")));

        NavigateToScreenshotUsageAction action = new NavigateToScreenshotUsageAction();
        action.actionPerformed(doTestActionEvent(screenshot));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(277);
    }

    public void testNavigatesToNonDefaultScreenshotValidation() {
        PsiFile specFile = myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[non-default].png")));

        NavigateToScreenshotUsageAction action = new NavigateToScreenshotUsageAction();
        action.actionPerformed(doTestActionEvent(screenshot));

        assertThat(FileEditorManager.getInstance(getProject()).isFileOpen(specFile.getVirtualFile())).isTrue();
        assertThat(myFixture.getCaretOffset()).isEqualTo(401);
    }

    public void testShowDialogOfNoValidationLinkedToTheScreenshot() {
        myFixture.configureByFile("tests/wdio/NavigateToScreenshotUsage-spec.js");
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png")));

        NavigateToScreenshotUsageAction action = new NavigateToScreenshotUsageAction();
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> action.actionPerformed(doTestActionEvent(screenshot)))
            .withMessage("There is no validation linked to this screenshot.\n" +
                "Either it has been removed entirely or is now referencing this image by a different name.");
    }

    public void testShowNoSpecFileToNavigateToDialog() {
        PsiFile screenshot = PsiManager.getInstance(getProject()).findFile(myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[not-referenced].png")));

        NavigateToScreenshotUsageAction action = new NavigateToScreenshotUsageAction();

        assertThat(FileEditorManager.getInstance(getProject()).hasOpenFiles()).isFalse();
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> action.actionPerformed(doTestActionEvent(screenshot)))
            .withMessage("There is no spec file available to navigate to. It may have been removed.");
    }

    private AnActionEvent doTestActionEvent(PsiFile screenshot) {
        return TestActionEvent.createTestEvent(dataId -> {
            if (CommonDataKeys.PSI_FILE.is(dataId)) return screenshot;
            if (CommonDataKeys.PROJECT.is(dataId)) return getProject();
            if (PlatformDataKeys.CONTEXT_COMPONENT.is(dataId)) return wdioTree();
            return null;
        });
    }

    private TerraWdioTree wdioTree() {
        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        TreeSpecNode spec = ((TreeModelDataRoot) treeModel.getRoot()).getSpecs().get(0);
        TreeScreenshotNode screenshot = spec.getScreenshot(0);
        tree.addSelectionPath(new TreePath(new Object[]{treeModel.getRoot(), spec, screenshot}));
        return tree;
    }
}
