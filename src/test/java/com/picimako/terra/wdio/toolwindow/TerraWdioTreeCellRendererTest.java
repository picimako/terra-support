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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link TerraWdioTreeCellRenderer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TerraWdioTreeCellRendererTest {

    @Mock
    private JTree tree;

    private TerraWdioTreeCellRenderer renderer;

    @Before
    public void before() {
        renderer = new TerraWdioTreeCellRenderer();
        renderer.setFont(new Font("font", Font.ITALIC, 14));
    }

    @Test
    public void shouldSetFontAndTextForModelDataRoot() {
        TerraWdioTreeModelDataRoot dataRoot = new TerraWdioTreeModelDataRoot("root");

        JLabel component = (JLabel) renderer.getTreeCellRendererComponent(tree, dataRoot, false, false, false, 1, false);

        assertThat(component.getFont()).isEqualTo(new Font("font", Font.PLAIN, 14));
        assertThat(component.getText()).isEqualTo("root (0 specs, 0 screenshots)");
    }

    @Test
    public void shouldSetFontAndTextForSpecNode() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec");

        JLabel component = (JLabel) renderer.getTreeCellRendererComponent(tree, spec, false, false, false, 1, false);

        assertThat(component.getFont()).isEqualTo(new Font("font", Font.PLAIN, 14));
        assertThat(component.getText()).isEqualTo("spec");
    }

    @Test
    public void shouldMarkSpecNodeAsDiff() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec");
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot.addDiff(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);

        JLabel component = (JLabel) renderer.getTreeCellRendererComponent(tree, spec, false, false, false, 1, false);

        assertThat(component.getFont()).isEqualTo(new Font("font", Font.BOLD, 14));
        assertThat(component.getText()).isEqualTo("spec (1)");
    }

    @Test
    public void shouldSetFontAndTextForScreenshotNode() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");

        JLabel component = (JLabel) renderer.getTreeCellRendererComponent(tree, screenshot, false, false, false, 1, false);

        assertThat(component.getFont()).isEqualTo(new Font("font", Font.PLAIN, 14));
        assertThat(component.getText()).isEqualTo("screenshot (0)");
    }

    @Test
    public void shouldScreenshotNodeAsDiff() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot.addDiff(mock(VirtualFile.class));

        JLabel component = (JLabel) renderer.getTreeCellRendererComponent(tree, screenshot, false, false, false, 1, false);

        assertThat(component.getFont()).isEqualTo(new Font("font", Font.BOLD, 14));
        assertThat(component.getText()).isEqualTo("screenshot (0)");
    }
}
