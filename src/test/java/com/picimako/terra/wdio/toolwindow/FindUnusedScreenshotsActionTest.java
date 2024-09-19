//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.TestActionEvent;

import com.picimako.terra.TerraToolkitTestCase;
import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Unit test for {@link FindUnusedScreenshotsAction}.
 */
public class FindUnusedScreenshotsActionTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testMarksScreenshotsUnused() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[default].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/unused[default].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/FindUnusedScreenshot-spec/used[fromlatest].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/FindUnusedScreenshot-spec/unused[fromlatest].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/FindUnusedScreenshot-spec/used[fromdiff].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/FindUnusedScreenshot-spec/unused[fromdiff].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[partialid].png"));

        myFixture.copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");

        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        FindUnusedScreenshotsAction action = new FindUnusedScreenshotsAction(tree, getProject());

        TreeSpecNode relatedFindUnusedScreenshotSpec = ((TreeModelDataRoot) treeModel.getRoot()).getSpecs().getFirst();
        assertThat(relatedFindUnusedScreenshotSpec.getScreenshots().stream().noneMatch(TreeScreenshotNode::isUnused)).isTrue();

        action.actionPerformed(TestActionEvent.createTestEvent());

        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[default].png").get().isUnused()).isFalse();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("unused[default].png").get().isUnused()).isTrue();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[fromlatest].png").get().isUnused()).isFalse();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("unused[fromlatest].png").get().isUnused()).isTrue();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[fromdiff].png").get().isUnused()).isFalse();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("unused[fromdiff].png").get().isUnused()).isTrue();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[partialid].png").get().isUnused()).isFalse();
    }

    public void testUnrelatedIsUnused() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[default].png"));

        myFixture.copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");

        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        FindUnusedScreenshotsAction action = new FindUnusedScreenshotsAction(tree, getProject());

        TreeSpecNode nonRelatedSomeSpec = ((TreeModelDataRoot) treeModel.getRoot()).getSpecs().getFirst();
        assertThat(nonRelatedSomeSpec.getScreenshots().stream().noneMatch(TreeScreenshotNode::isUnused)).isTrue();

        action.actionPerformed(TestActionEvent.createTestEvent());

        //This is marked as unused because there is no spec file present that references this image
        //Although FindUnusedScreenshot-spec has a validation for terra_screenshot[collect].png, it doesn't have an image for this spec
        assertThat(nonRelatedSomeSpec.findScreenshotNodeByName("terra_screenshot[collect].png").get().isUnused()).isTrue();
    }
}
