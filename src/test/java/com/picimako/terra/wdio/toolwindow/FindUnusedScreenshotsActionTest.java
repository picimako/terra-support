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

/**
 * Unit test for {@link FindUnusedScreenshotsAction}.
 */
public class FindUnusedScreenshotsActionTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testMarksScreenshotsUnused() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"));

        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/used[default].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/unused[default].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/FindUnusedScreenshot-spec/used[fromlatest].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/FindUnusedScreenshot-spec/unused[fromlatest].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/FindUnusedScreenshot-spec/used[fromdiff].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/FindUnusedScreenshot-spec/unused[fromdiff].png"));

        myFixture.copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");

        TerraWdioTreeModel treeModel = new TerraWdioTreeModel(getProject());
        TerraWdioTree tree = new TerraWdioTree(treeModel);
        FindUnusedScreenshotsAction action = new FindUnusedScreenshotsAction(tree);

        TerraWdioTreeSpecNode nonRelatedSomeSpec = ((TerraWdioTreeModelDataRoot) treeModel.getRoot()).getSpecs().get(0);
        assertThat(nonRelatedSomeSpec.getScreenshots().stream().noneMatch(TerraWdioTreeScreenshotNode::isUnused)).isTrue();

        TerraWdioTreeSpecNode relatedFindUnusedScreenshotSpec = ((TerraWdioTreeModelDataRoot) treeModel.getRoot()).getSpecs().get(1);
        assertThat(relatedFindUnusedScreenshotSpec.getScreenshots().stream().noneMatch(TerraWdioTreeScreenshotNode::isUnused)).isTrue();

        action.actionPerformed(new TestActionEvent());

        //This validates that having a screenshot validation in code for this screenshot name in another spec
        //and not having an image with this name for that other spec, doesn't mark this image as unused, since it might be used by
        //its own spec file.
        assertThat(nonRelatedSomeSpec.findScreenshotNodeByName("terra-_screenshot--[with-_-replaced-_-characters_-].png").get().isUnused()).isTrue();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[default].png").get().isUnused()).isFalse();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("unused[default].png").get().isUnused()).isTrue();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[fromlatest].png").get().isUnused()).isFalse();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("unused[fromlatest].png").get().isUnused()).isTrue();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("used[fromdiff].png").get().isUnused()).isFalse();
        assertThat(relatedFindUnusedScreenshotSpec.findScreenshotNodeByName("unused[fromdiff].png").get().isUnused()).isTrue();

    }

    private String reference(String path) {
        return "tests/wdio/__snapshots__/reference" + path;
    }

    private String diff(String path) {
        return "tests/wdio/__snapshots__/diff" + path;
    }

    private String latest(String path) {
        return "tests/wdio/__snapshots__/latest" + path;
    }
}
