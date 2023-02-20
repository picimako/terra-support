//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
