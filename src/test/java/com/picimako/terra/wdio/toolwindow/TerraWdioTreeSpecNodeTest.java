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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link TerraWdioTreeSpecNode}.
 */
public class TerraWdioTreeSpecNodeTest {

    private TerraWdioTreeSpecNode specNode;
    private TerraWdioTreeScreenshotNode screenshotNode1;
    private TerraWdioTreeScreenshotNode screenshotNode2;

    @Before
    public void setup() {
        specNode = new TerraWdioTreeSpecNode("spec node");
        screenshotNode1 = new TerraWdioTreeScreenshotNode("screenshot node 1");
        screenshotNode2 = new TerraWdioTreeScreenshotNode("node screenshot 2");
    }

    //addScreenshot(TerraWdioTreeScreenshotNode)

    @Test
    public void shouldAddScreenshot() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.getScreenshots()).containsExactly(screenshotNode1, screenshotNode2);
    }

    //getScreenshot(int)

    @Test
    public void shouldGetNthScreenshot() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.getScreenshot(0)).isSameAs(screenshotNode1);
        assertThat(specNode.getScreenshot(1)).isSameAs(screenshotNode2);
    }

    //findScreenshotNodeByName(String)

    @Test
    public void shouldReturnFoundScreenshotByName() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.findScreenshotNodeByName("node screenshot 2")).containsSame(screenshotNode2);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenScreenshotIsNotFoundByName() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.findScreenshotNodeByName("image 4")).isEmpty();
    }

    //hasScreenshotNodeForName(String)

    @Test
    public void shouldReturnTrueWhenHasScreenshotForName() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.hasScreenshotNodeForName("node screenshot 2")).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenDoesntHaveScreenshotForName() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.hasScreenshotNodeForName("image 4")).isFalse();
    }

    //screenshotCount

    @Test
    public void shouldReturnScreenshotCount() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode.screenshotCount()).isEqualTo(2);
    }

    //reorderScreenshotsAlphabeticallyByDisplayName

    @Test
    public void shouldReorderScreenshotsAlphabetically() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        specNode.reorderScreenshotsAlphabeticallyByDisplayName();

        assertThat(specNode.getScreenshots()).containsExactly(screenshotNode2, screenshotNode1);
    }

    //toString

    @Test
    public void shouldReturnToStringWithoutScreenshotCount() {
        assertThat(specNode).hasToString("spec node");
    }

    @Test
    public void shouldReturnToStringWithScreenshotCount() {
        specNode.addScreenshot(screenshotNode1);
        specNode.addScreenshot(screenshotNode2);

        assertThat(specNode).hasToString("spec node (2)");
    }
}
