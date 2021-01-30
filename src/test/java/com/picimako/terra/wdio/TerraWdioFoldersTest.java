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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //This is necessary to avoid test cases interfering with each other due to the cache value
        TerraWdioFolders.clearWdioRootCache();
    }

    // projectWdioRoot

    public void testReturnVirtualFileForProjectWdioRoot() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        VirtualFile wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());

        runAssertions(wdioRoot, "/tests/wdio");
    }

    public void testReturnNoRootDirWhenProjectWdioRootDoesntExist() {
        assertThat(TerraWdioFolders.projectWdioRoot(getProject())).isNull();
    }

    // getTestRoot

    public void testReturnTestRootDirectory() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.getTestRoot(getProject(), "wdio")).isNotNull();
    }

    public void testReturnNoTestRootWhenNoTestRootExists() {
        assertThat(TerraWdioFolders.getTestRoot(getProject(), "wdio")).isNull();
    }

    // wdioRootRelativePath

    public void testReturnWdioRootRelativePath() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.wdioRootRelativePath(getProject())).isEqualTo("tests/wdio");
    }

    // getRelativePathToProjectDir

    public void testReturnRelativePath() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.getRelativePathToProjectDir(getProject(), ProjectUtil.guessProjectDir(getProject()))).isEqualTo("");
    }

    // collectSpecFoldersInside

    public void testCollectSpecFolders() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        List<VirtualFile> filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(TerraWdioFolders.projectWdioRoot(getProject()));
        List<VirtualFile> diffs = TerraWdioFolders.collectSpecFoldersInside("diff", filesAndFoldersInWdioRoot).collect(toList());

        assertThat(diffs).hasSize(1);
        assertThat(diffs.get(0).getPath()).isEqualTo("/src/tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec");
    }

    // collectSpecFiles

    public void testCollectSpecFilesFromAll() {
        myFixture.copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");
        myFixture.copyFileToProject("tests/wdio/nested/anEmpty-spec.js");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        List<VirtualFile> filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(TerraWdioFolders.projectWdioRoot(getProject()));
        Set<VirtualFile> specFiles = TerraWdioFolders.collectSpecFiles(filesAndFoldersInWdioRoot);

        assertThat(specFiles).hasSize(2);
        assertThat(specFiles.iterator().next().getPath()).matches("/src/tests/wdio/(FindUnusedScreenshot-spec.js|nested/anEmpty-spec.js)");
        assertThat(specFiles.iterator().next().getPath()).matches("/src/tests/wdio/(FindUnusedScreenshot-spec.js|nested/anEmpty-spec.js)");
    }

    // diffImageForLatest

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

    // latestImageForReference

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

    // specFolderIdentifier

    public void testSpecFolderIdentifierFolderNoNestedFolder() {
        VirtualFile parent = myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png").getParent();

        String id = TerraWdioFolders.specFolderIdentifier(parent, getProject());

        assertThat(id).isEqualTo("some-spec");
    }

    public void testSpecFolderIdentifierFolderOneLevelNestedFolder() {
        VirtualFile parent = myFixture.copyFileToProject("tests/wdio/nested/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png").getParent();

        String id = TerraWdioFolders.specFolderIdentifier(parent, getProject());

        assertThat(id).isEqualTo("nested/some-spec");
    }

    public void testSpecFolderIdentifierFolderTwoLevelNestedFolder() {
        VirtualFile parent = myFixture.copyFileToProject("tests/wdio/nested/folder/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png").getParent();

        String id = TerraWdioFolders.specFolderIdentifier(parent, getProject());

        assertThat(id).isEqualTo("nested/folder/some-spec");
    }

    // isInWdioFiles

    public void testInWdioFilesDirectly() {
        VirtualFile specFile = myFixture.copyFileToProject("tests/wdio/MissingScreenshots-spec.js");

        assertThat(TerraWdioFolders.isInWdioFiles(specFile, getProject())).isTrue();
    }

    public void testInWdioFilesIndirectly() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/nested/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isInWdioFiles(screenshot, getProject())).isTrue();
    }

    public void testNotInWdioFiles() {
        VirtualFile wdioConf = myFixture.copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isInWdioFiles(wdioConf, getProject())).isFalse();
    }

    public void testNotInWdioFilesWhenNoWdioRootExists() {
        assertThat(TerraWdioFolders.isInWdioFiles(null, getProject())).isFalse();
    }

    //collectSpecFiles

    public void testCollectSpecFiles() {
        myFixture.copyFileToProject("tests/wdio/nameResolution/resolveName-spec.js");
        myFixture.copyFileToProject("tests/wdio/nameResolution/resolveNameNoParentDescribe-spec.js");
        myFixture.copyFileToProject("tests/wdio/nameResolution/resolveNameNoParentDescribeName-spec.js");
        myFixture.copyFileToProject("tests/wdio/nested/anEmpty-spec.js");
        myFixture.copyFileToProject("tests/wdio/nested/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        List<PsiFile> specFiles = new ArrayList<>();
        PsiDirectory projectRoot = PsiManager.getInstance(getProject()).findDirectory(ProjectUtil.guessProjectDir(getProject()));
        TerraWdioFolders.collectSpecFiles(projectRoot, specFiles);

        List<String> specFilePaths = specFiles.stream().map(file -> file.getVirtualFile().getPath()).collect(toList());

        assertThat(specFilePaths).containsExactlyInAnyOrder("/src/tests/wdio/nameResolution/resolveName-spec.js",
            "/src/tests/wdio/nameResolution/resolveNameNoParentDescribe-spec.js",
            "/src/tests/wdio/nameResolution/resolveNameNoParentDescribeName-spec.js",
            "/src/tests/wdio/nested/anEmpty-spec.js");
    }

    //isSnapshotsDirectory

    public void testIsSnapshotDirectory() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");
        PsiDirectory snapshots = PsiManager.getInstance(getProject())
            .findDirectory(TerraWdioFolders.projectWdioRoot(getProject()).findChild("__snapshots__"));

        assertThat(TerraWdioFolders.isSnapshotsDirectory(snapshots)).isTrue();
    }

    public void testIsNotSnapshotDirectory() {
        PsiDirectory projectRoot = PsiManager.getInstance(getProject()).findDirectory(ProjectUtil.guessProjectDir(getProject()));

        assertThat(TerraWdioFolders.isSnapshotsDirectory(projectRoot)).isFalse();
    }

    // isDiffScreenshot

    public void testIsDiffScreenshot() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isDiffScreenshot(screenshot, getProject())).isTrue();
    }

    public void testIsNotDiffScreenshotNotInWdioFiles() {
        VirtualFile wdioConf = myFixture.copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isDiffScreenshot(wdioConf, getProject())).isFalse();
    }

    public void testIsNotDiffScreenshot() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isDiffScreenshot(screenshot, getProject())).isFalse();
    }

    // isLatestScreenshot

    public void testIsLatestScreenshot() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isLatestScreenshot(screenshot, getProject())).isTrue();
    }

    public void testIsNotLatestScreenshotNotInWdioFiles() {
        VirtualFile wdioConf = myFixture.copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isLatestScreenshot(wdioConf, getProject())).isFalse();
    }

    public void testIsNotLatestScreenshot() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isLatestScreenshot(screenshot, getProject())).isFalse();
    }

    // isReferenceScreenshot

    public void testIsReferenceScreenshot() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isReferenceScreenshot(screenshot, getProject())).isTrue();
    }

    public void testIsNotReferenceScreenshotNotInWdioFiles() {
        VirtualFile wdioConf = myFixture.copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isReferenceScreenshot(wdioConf, getProject())).isFalse();
    }

    public void testIsNotReferenceScreenshot() {
        VirtualFile screenshot = myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isReferenceScreenshot(screenshot, getProject())).isFalse();
    }

    private void runAssertions(VirtualFile file, String path) {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(file).isNotNull();
        softly.assertThat(file.exists()).isTrue();
        softly.assertThat(file.getPath()).endsWith(path);
        softly.assertAll();
    }
}
