//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Unit test for {@link TerraWdioTreeNode}.
 */
public class TerraWdioTreeNodeTest {

    @Test
    public void shouldBeSpec() {
        TreeSpecNode spec = new TreeSpecNode("spec", null);

        assertThat(TerraWdioTreeNode.isSpec(spec)).isTrue();
    }

    @Test
    public void shouldNotBeSpec() {
        TreeModelDataRoot root = new TreeModelDataRoot("root", null);

        assertThat(TerraWdioTreeNode.isSpec(root)).isFalse();
    }

    @Test
    public void shouldBeScreenshot() {
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", null);

        assertThat(TerraWdioTreeNode.isScreenshot(screenshot)).isTrue();
    }

    @Test
    public void shouldNotBeScreenshot() {
        TreeSpecNode spec = new TreeSpecNode("spec", null);

        assertThat(TerraWdioTreeNode.isScreenshot(spec)).isFalse();
    }
}
