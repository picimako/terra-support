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

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Unit test for {@link TreeSpecNode}.
 */
public class TreeSpecNodeTest extends BasePlatformTestCase {

    private TreeSpecNode specNode;
    private TreeScreenshotNode screenshotNode1;
    private TreeScreenshotNode screenshotNode2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        specNode = new TreeSpecNode("spec node", getProject());
        screenshotNode1 = new TreeScreenshotNode("screenshot node 1", getProject());
        screenshotNode2 = new TreeScreenshotNode("node screenshot 2", getProject());

        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);
    }

    //addScreenshot(TerraWdioTreeScreenshotNode)

    public void testAddScreenshot() {
        assertThat(specNode.getScreenshots()).containsExactly(screenshotNode1, screenshotNode2);
    }

    //getScreenshot(int)

    public void testGetNthScreenshot() {
        assertThat(specNode.getScreenshot(0)).isSameAs(screenshotNode1);
        assertThat(specNode.getScreenshot(1)).isSameAs(screenshotNode2);
    }

    //findScreenshotNodeByName(String)

    public void testReturnFoundScreenshotByName() {
        assertThat(specNode.findScreenshotNodeByName("node screenshot 2")).containsSame(screenshotNode2);
    }

    public void testReturnEmptyOptionalWhenScreenshotIsNotFoundByName() {
        assertThat(specNode.findScreenshotNodeByName("image 4")).isEmpty();
    }

    //hasScreenshotNodeForName(String)

    public void testReturnTrueWhenHasScreenshotForName() {
        assertThat(specNode.hasScreenshotNodeForName("node screenshot 2")).isTrue();
    }

    public void testReturnFalseWhenDoesntHaveScreenshotForName() {
        assertThat(specNode.hasScreenshotNodeForName("image 4")).isFalse();
    }

    //screenshotCount

    public void testReturnScreenshotCount() {
        assertThat(specNode.screenshotCount()).isEqualTo(2);
    }

    //reorderScreenshotsAlphabeticallyByDisplayName

    public void testReorderScreenshotsAlphabetically() {
        specNode.reorderScreenshotsAlphabeticallyByDisplayName();

        assertThat(specNode.getScreenshots()).containsExactly(screenshotNode2, screenshotNode1);
    }

    //toString

    public void testReturnToStringWithoutScreenshotCount() {
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = false;
        specNode.getScreenshots().clear();

        assertThat(specNode).hasToString("spec node");
    }

    public void testReturnToStringWithScreenshotCount() {
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = true;

        assertThat(specNode).hasToString("spec node (2)");
    }
}
