//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;

import com.intellij.testFramework.TestActionEvent;

import com.picimako.terra.TerraToolkitTestCase;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
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
        copyFilesToProject(
            reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[default].png"),
            reference("/en/chrome_huge/FindUnusedScreenshot-spec/unused[default].png"),
            latest("/en/chrome_huge/FindUnusedScreenshot-spec/used[fromlatest].png"),
            latest("/en/chrome_huge/FindUnusedScreenshot-spec/unused[fromlatest].png"),
            diff("/en/chrome_huge/FindUnusedScreenshot-spec/used[fromdiff].png"),
            diff("/en/chrome_huge/FindUnusedScreenshot-spec/unused[fromdiff].png"),
            reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[partialid].png"),
            "tests/wdio/FindUnusedScreenshot-spec.js"
        );

        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        var action = new FindUnusedScreenshotsAction(tree, getProject());

        TreeSpecNode relatedFindUnusedScreenshotSpec = ((TreeModelDataRoot) treeModel.getRoot()).getSpecs().getFirst();
        assertThat(relatedFindUnusedScreenshotSpec.getScreenshots().stream().noneMatch(TreeScreenshotNode::isUnused)).isTrue();

        action.actionPerformed(TestActionEvent.createTestEvent());

        var screenshotsToUnused = Map.of(
            "used[default].png", false,
            "unused[default].png", true,
            "used[fromlatest].png", false,
            "unused[fromlatest].png", true,
            "used[fromdiff].png", false,
            "unused[fromdiff].png", true,
            "used[partialid].png", false
        );
        assertSoftly(s ->
            screenshotsToUnused.forEach((name, unused) ->
                s.assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName(name).get().isUnused()).isEqualTo(unused)));
    }

    public void testUnrelatedIsUnused() {
        copyFilesToProject(
            reference("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[collect].png"),
            reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[default].png"),
            "tests/wdio/FindUnusedScreenshot-spec.js"
        );

        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        var action = new FindUnusedScreenshotsAction(tree, getProject());

        TreeSpecNode nonRelatedSomeSpec = ((TreeModelDataRoot) treeModel.getRoot()).getSpecs().getFirst();
        assertThat(nonRelatedSomeSpec.getScreenshots().stream().noneMatch(TreeScreenshotNode::isUnused)).isTrue();

        action.actionPerformed(TestActionEvent.createTestEvent());

        //This is marked as unused because there is no spec file present that references this image
        //Although FindUnusedScreenshot-spec has a validation for terra_screenshot[collect].png, it doesn't have an image for this spec
        assertThat(nonRelatedSomeSpec.findScreenshotNodeByName("terra_screenshot[collect].png").get().isUnused()).isTrue();
    }
}
