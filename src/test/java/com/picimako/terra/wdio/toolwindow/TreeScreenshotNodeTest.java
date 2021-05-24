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
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;

/**
 * Unit test for {@link TreeScreenshotNode}.
 */
public class TreeScreenshotNodeTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = true;
    }

    public void testReturnToStringWithZeroReferenceCount() {
        TreeScreenshotNode screenshotNode = new TreeScreenshotNode("screenshot node", getProject());

        assertThat(screenshotNode).hasToString("screenshot node (0)");
    }

    public void testReturnToStringWithMoreThanZeroReferenceCount() {
        TreeScreenshotNode screenshotNode = new TreeScreenshotNode("screenshot node", getProject());

        screenshotNode.addReference(mock(VirtualFile.class));
        screenshotNode.addReference(mock(VirtualFile.class));

        assertThat(screenshotNode).hasToString("screenshot node (2)");
    }

    public void testHaveLatest() {
        TreeScreenshotNode node = new TreeScreenshotNode("dummy", null);
        node.addLatest(mock(VirtualFile.class));

        assertThat(node.hasLatest()).isTrue();
    }

    public void testNotHaveLatest() {
        TreeScreenshotNode node = new TreeScreenshotNode("dummy", null);

        assertThat(node.hasLatest()).isFalse();
    }

    public void testHaveDiff() {
        TreeScreenshotNode node = new TreeScreenshotNode("dummy", null);
        node.addDiff(mock(VirtualFile.class));

        assertThat(node.hasDiff()).isTrue();
    }

    public void testNotHaveDiff() {
        TreeScreenshotNode node = new TreeScreenshotNode("dummy", null);

        assertThat(node.hasDiff()).isFalse();
    }
}
