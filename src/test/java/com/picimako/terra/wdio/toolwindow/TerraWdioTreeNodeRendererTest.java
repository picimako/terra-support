//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.*;
import javax.swing.*;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Unit test for {@link TerraWdioTree.TerraWdioNodeRenderer}.
 */
public class TerraWdioTreeNodeRendererTest extends BasePlatformTestCase {

    private TerraWdioTree.TerraWdioNodeRenderer renderer;
    private TerraWdioTree tree;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        tree = new TerraWdioTree(treeModel);
        renderer = new TerraWdioTree.TerraWdioNodeRenderer();
        renderer.setFont(new Font("font", Font.ITALIC, 14));
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = true;
    }

    public void testSetFontAndTextForModelDataRoot() {
        TreeModelDataRoot dataRoot = new TreeModelDataRoot("root", getProject());

        customizeComponent(tree, dataRoot);
        validateComponent(new Font("font", Font.PLAIN, 14), "Wdio Resources (0 specs, 0 screenshots)");
    }

    public void testSetFontAndTextForSpecNode() {
        TreeSpecNode spec = new TreeSpecNode("spec", getProject());

        customizeComponent(tree, spec);
        validateComponent(new Font("font", Font.PLAIN, 14), "spec");
    }

    public void testMarkSpecNodeAsDiff() {
        TreeSpecNode spec = new TreeSpecNode("spec", getProject());
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());
        screenshot.addDiff(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);

        customizeComponent(tree, spec);
        validateComponent(new Font("font", Font.BOLD, 14), "spec (1)");
    }

    public void testSetFontAndTextForScreenshotNode() {
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());

        customizeComponent(tree, screenshot);
        validateComponent(new Font("font", Font.PLAIN, 14), "screenshot (0)");
    }

    public void testScreenshotNodeAsDiff() {
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());
        screenshot.addDiff(mock(VirtualFile.class));

        customizeComponent(tree, screenshot);
        validateComponent(new Font("font", Font.BOLD, 14), "screenshot (0)");
    }

    private void customizeComponent(JTree tree, TerraWdioTreeNode node) {
        renderer.customizeCellRenderer(tree, node, false, false, false, 1, false);
    }

    private void validateComponent(Font font, String text) {
        assertThat(renderer.getFont()).isEqualTo(font);
        assertThat(renderer.getCharSequence(true)).isEqualTo(text);
    }
}
