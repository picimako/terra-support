/*
 * Copyright 2021 Tamás Balog
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

import com.intellij.testFramework.TestActionEvent;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Unit test for {@link FindUnusedScreenshotsAction}.
 */
public class FindUnusedScreenshotsActionTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testMarksScreenshotsUnused() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_medium/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");

        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra_screenshot[single].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        myFixture.copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");

        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        FindUnusedScreenshotsAction action = new FindUnusedScreenshotsAction(tree);

        TerraWdioTreeModelDataRoot root = (TerraWdioTreeModelDataRoot) treeModel.getRoot();
        TerraWdioTreeSpecNode spec = root.getSpecs().get(0);
        assertThat(spec.getScreenshots().stream().noneMatch(TerraWdioTreeScreenshotNode::isUnused)).isTrue();

        action.actionPerformed(new TestActionEvent());

        assertThat(spec.findScreenshotNodeByName("terra-_screenshot--[with-_-replaced-_-characters_-].png").get().isUnused()).isFalse();
        assertThat(spec.findScreenshotNodeByName("terra_screenshot[single].png").get().isUnused()).isTrue();
        assertThat(spec.findScreenshotNodeByName("testimage[default].png").get().isUnused()).isTrue();
    }
}
