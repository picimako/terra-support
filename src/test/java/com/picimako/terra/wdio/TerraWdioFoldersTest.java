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

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.assertj.core.api.SoftAssertions;

/**
 * Unit test for {@link TerraWdioFolders}.
 */
public class TerraWdioFoldersTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testReturnVirtualFileForProjectWdioRoot() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        VirtualFile wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());

        runAssertions(wdioRoot, "/tests/wdio");
    }

    public void testReturnNullForWhenProjectWdioRootDoesntExist() {
        assertThat(TerraWdioFolders.projectWdioRoot(getProject())).isNull();
    }

    public void testGetDiffImageForLatestImage() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");
        VirtualFile wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());
        TerraWdioFolders.setWdioTestRootPath(wdioRoot.getPath());
        VirtualFile latest = FilenameIndex.getVirtualFilesByName(getProject(), "testimage[default].png", GlobalSearchScope.projectScope(getProject()))
            .stream()
            .filter(image -> image.getPath().endsWith("latest/en/chrome_huge/some-spec/testimage[default].png"))
            .findFirst()
            .get();

        VirtualFile diffImage = TerraWdioFolders.diffImageForLatest(getProject(), latest);

        runAssertions(diffImage, "/diff/en/chrome_huge/some-spec/testimage[default].png");
    }

    public void testGetLatestImageForReferenceImage() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");
        VirtualFile wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());
        TerraWdioFolders.setWdioTestRootPath(wdioRoot.getPath());
        VirtualFile latest = FilenameIndex.getVirtualFilesByName(getProject(), "testimage[default].png", GlobalSearchScope.projectScope(getProject()))
            .stream()
            .filter(image -> image.getPath().endsWith("reference/en/chrome_huge/some-spec/testimage[default].png"))
            .findFirst()
            .get();

        VirtualFile latestImage = TerraWdioFolders.latestImageForReference(getProject(), latest);

        runAssertions(latestImage, "/latest/en/chrome_huge/some-spec/testimage[default].png");
    }

    private void runAssertions(VirtualFile file, String path) {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(file).isNotNull();
        softly.assertThat(file.exists()).isTrue();
        softly.assertThat(file.getPath()).endsWith(path);
        softly.assertAll();
    }
}
