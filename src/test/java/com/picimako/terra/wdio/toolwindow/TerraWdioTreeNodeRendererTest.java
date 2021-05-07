/*
 * Copyright 2020 Tamás Balog
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

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.*;
import javax.swing.*;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link com.picimako.terra.wdio.toolwindow.TerraWdioTree.TerraWdioNodeRenderer}.
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
        TerraWdioTreeModelDataRoot dataRoot = new TerraWdioTreeModelDataRoot("root", getProject());

        customizeComponent(tree, dataRoot);
        validateComponent(new Font("font", Font.PLAIN, 14), "root (0 specs, 0 screenshots)");
    }

    public void testSetFontAndTextForSpecNode() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());

        customizeComponent(tree, spec);
        validateComponent(new Font("font", Font.PLAIN, 14), "spec");
    }

    public void testMarkSpecNodeAsDiff() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addDiff(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);

        customizeComponent(tree, spec);
        validateComponent(new Font("font", Font.BOLD, 14), "spec (1)");
    }

    public void testSetFontAndTextForScreenshotNode() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());

        customizeComponent(tree, screenshot);
        validateComponent(new Font("font", Font.PLAIN, 14), "screenshot (0)");
    }

    public void testScreenshotNodeAsDiff() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
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
