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
 * Unit test for {@link TerraWdioTreeCellRenderer}.
 */
public class TerraWdioTreeCellRendererTest extends BasePlatformTestCase {

    private TerraWdioTreeCellRenderer renderer;
    private TerraWdioTree tree;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        tree = new TerraWdioTree(treeModel);
        renderer = new TerraWdioTreeCellRenderer();
        renderer.setFont(new Font("font", Font.ITALIC, 14));
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = true;
    }

    public void testSetFontAndTextForModelDataRoot() {
        TerraWdioTreeModelDataRoot dataRoot = new TerraWdioTreeModelDataRoot("root", getProject());

        validateComponent(getComponent(tree, dataRoot), new Font("font", Font.PLAIN, 14), "root (0 specs, 0 screenshots)");
    }

    public void testSetFontAndTextForSpecNode() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());

        validateComponent(getComponent(tree, spec), new Font("font", Font.PLAIN, 14), "spec");
    }

    public void testMarkSpecNodeAsDiff() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addDiff(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);

        validateComponent(getComponent(tree, spec), new Font("font", Font.BOLD, 14), "spec (1)");
    }

    public void testSetFontAndTextForScreenshotNode() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());

        validateComponent(getComponent(tree, screenshot), new Font("font", Font.PLAIN, 14), "screenshot (0)");
    }

    public void testScreenshotNodeAsDiff() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addDiff(mock(VirtualFile.class));

        validateComponent(getComponent(tree, screenshot), new Font("font", Font.BOLD, 14), "screenshot (0)");
    }

    private JLabel getComponent(JTree tree, TerraWdioTreeNode node) {
        return (JLabel) renderer.getTreeCellRendererComponent(tree, node, false, false, false, 1, false);
    }

    private void validateComponent(JLabel component, Font font, String text) {
        assertThat(component.getFont()).isEqualTo(font);
        assertThat(component.getText()).isEqualTo(text);
    }
}
