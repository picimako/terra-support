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

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Test;

/**
 * Unit test for {@link TerraWdioTreeScreenshotNode}.
 */
public class TerraWdioTreeScreenshotNodeTest {

    @Test
    public void shouldReturnToStringWithZeroReferenceCount() {
        TerraWdioTreeScreenshotNode screenshotNode = new TerraWdioTreeScreenshotNode("screenshot node");

        assertThat(screenshotNode).hasToString("screenshot node (0)");
    }

    @Test
    public void shouldReturnToStringWithMoreThanZeroReferenceCount() {
        TerraWdioTreeScreenshotNode screenshotNode = new TerraWdioTreeScreenshotNode("screenshot node");

        screenshotNode.addReference(mock(VirtualFile.class));
        screenshotNode.addReference(mock(VirtualFile.class));

        assertThat(screenshotNode).hasToString("screenshot node (2)");
    }

    @Test
    public void shouldHaveLatest() {
        TerraWdioTreeScreenshotNode node = new TerraWdioTreeScreenshotNode("dummy");
        node.addLatest(mock(VirtualFile.class));

        assertThat(node.hasLatest()).isTrue();
    }

    @Test
    public void shouldNotHaveLatest() {
        TerraWdioTreeScreenshotNode node = new TerraWdioTreeScreenshotNode("dummy");

        assertThat(node.hasLatest()).isFalse();
    }

    @Test
    public void shouldHaveDiff() {
        TerraWdioTreeScreenshotNode node = new TerraWdioTreeScreenshotNode("dummy");
        node.addDiff(mock(VirtualFile.class));

        assertThat(node.hasDiff()).isTrue();
    }

    @Test
    public void shouldNotHaveDiff() {
        TerraWdioTreeScreenshotNode node = new TerraWdioTreeScreenshotNode("dummy");

        assertThat(node.hasDiff()).isFalse();
    }
}
