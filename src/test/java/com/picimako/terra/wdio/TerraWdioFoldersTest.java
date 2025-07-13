//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.assertj.core.api.SoftAssertions;

import com.picimako.terra.TerraSupportTestBase;

/**
 * Unit test for {@link TerraWdioFolders}.
 */
public class TerraWdioFoldersTest extends TerraSupportTestBase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    // projectWdioRoot

    public void testReturnVirtualFileForProjectWdioRoot() {
        copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        var wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());

        runAssertions(wdioRoot, "/tests/wdio");
    }

    public void testReturnNoRootDirWhenProjectWdioRootDoesntExist() {
        assertThat(TerraWdioFolders.projectWdioRoot(getProject())).isNull();
    }

    // getTestRoot

    public void testReturnTestRootDirectory() {
        copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(TerraWdioFolders.getTestRoot(getProject(), "wdio")).isNotNull();
    }

    public void testReturnNoTestRootWhenNoTestRootExists() {
        assertThat(TerraWdioFolders.getTestRoot(getProject(), "wdio")).isNull();
    }

    // wdioRootRelativePath

    public void testReturnWdioRootRelativePath() {
        copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(TerraWdioFolders.wdioRootRelativePath(getProject())).isEqualTo("tests/wdio");
    }

    // getRelativePathToProjectDir

    public void testReturnRelativePath() {
        copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(TerraWdioFolders.getRelativePathToProjectDir(getProject(), TerraWdioFolders.projectWdioRoot(getProject()))).isEqualTo("tests/wdio");
    }

    // collectSpecFiles

    public void testCollectSpecFilesFromAll() {
        copyFilesToProject(
            "tests/wdio/FindUnusedScreenshot-spec.js",
            "tests/wdio/nested/anEmpty-spec.js",
            "tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        var filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(TerraWdioFolders.projectWdioRoot(getProject()));
        var specFiles = TerraWdioFolders.collectSpecFiles(filesAndFoldersInWdioRoot);

        assertThat(specFiles).hasSize(2);
        assertThat(specFiles.iterator().next().getPath()).matches("/src/tests/wdio/(FindUnusedScreenshot-spec.js|nested/anEmpty-spec.js)");
        assertThat(specFiles.iterator().next().getPath()).matches("/src/tests/wdio/(FindUnusedScreenshot-spec.js|nested/anEmpty-spec.js)");
    }

    // diffImageForLatest

    public void testGetDiffImageForLatestImage() {
        copyFilesToProject(
            "tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png",
            "tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");
        var wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());
        TerraWdioFolders.setWdioTestRootPath(wdioRoot.getPath());
        var latest = FilenameIndex.getVirtualFilesByName("testimage[default].png", GlobalSearchScope.projectScope(getProject()))
            .stream()
            .filter(image -> image.getPath().endsWith("latest/en/chrome_huge/some-spec/testimage[default].png"))
            .findFirst()
            .get();

        var diffImage = TerraWdioFolders.diffImageForLatest(getProject(), latest);

        runAssertions(diffImage, "/diff/en/chrome_huge/some-spec/testimage[default].png");
    }

    // latestImageForReference

    public void testGetLatestImageForReferenceImage() {
        copyFilesToProject(
            "tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png",
            "tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");
        var wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());
        TerraWdioFolders.setWdioTestRootPath(wdioRoot.getPath());
        var latest = FilenameIndex.getVirtualFilesByName("testimage[default].png", GlobalSearchScope.projectScope(getProject()))
            .stream()
            .filter(image -> image.getPath().endsWith("reference/en/chrome_huge/some-spec/testimage[default].png"))
            .findFirst()
            .get();

        var latestImage = TerraWdioFolders.latestImageForReference(getProject(), latest);

        runAssertions(latestImage, "/latest/en/chrome_huge/some-spec/testimage[default].png");
    }

    // specFolderIdentifier

    public void testSpecFolderIdentifierFolderNoNestedFolder() {
        var parent = copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png").getParent();

        String id = TerraWdioFolders.specFolderIdentifier(parent, getProject());

        assertThat(id).isEqualTo("some-spec");
    }

    public void testSpecFolderIdentifierFolderOneLevelNestedFolder() {
        var parent = copyFileToProject("tests/wdio/nested/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png").getParent();

        String id = TerraWdioFolders.specFolderIdentifier(parent, getProject());

        assertThat(id).isEqualTo("nested/some-spec");
    }

    public void testSpecFolderIdentifierFolderTwoLevelNestedFolder() {
        var parent = copyFileToProject("tests/wdio/nested/folder/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png").getParent();

        String id = TerraWdioFolders.specFolderIdentifier(parent, getProject());

        assertThat(id).isEqualTo("nested/folder/some-spec");
    }

    // specFileIdentifier

    public void testSpecFileIdentifierFolderNoNestedFolder() {
        var specFile = copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");

        String id = TerraWdioFolders.specFileIdentifier(specFile, getProject());

        assertThat(id).isEqualTo("FindUnusedScreenshot-spec");
    }

    public void testSpecFileIdentifierFolderOneLevelNestedFolder() {
        var specFile = copyFileToProject("tests/wdio/nested/anEmpty-spec.js");

        String id = TerraWdioFolders.specFileIdentifier(specFile, getProject());

        assertThat(id).isEqualTo("nested/anEmpty-spec");
    }

    // isInWdioFiles

    public void testInWdioFilesDirectly() {
        var specFile = copyFileToProject("tests/wdio/MissingScreenshots-spec.js");

        assertThat(TerraWdioFolders.isInWdioFiles(specFile, getProject())).isTrue();
    }

    public void testInWdioFilesIndirectly() {
        var screenshot = copyFileToProject("tests/wdio/nested/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isInWdioFiles(screenshot, getProject())).isTrue();
    }

    public void testNotInWdioFiles() {
        var wdioConf = copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isInWdioFiles(wdioConf, getProject())).isFalse();
    }

    public void testNotInWdioFilesWhenNoWdioRootExists() {
        assertThat(TerraWdioFolders.isInWdioFiles(null, getProject())).isFalse();
    }

    //isInSnapshotsDirectory

    public void testInSnapshots() {
        var screenshot = copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png"));

        assertThat(TerraWdioFolders.isInSnapshotsDirectory(screenshot)).isTrue();
    }

    public void testNotInSnapshotsFilesForNoFile() {
        assertThat(TerraWdioFolders.isInSnapshotsDirectory(null)).isFalse();
    }

    public void testNotInSnapshots() {
        var wdioConf = copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isInSnapshotsDirectory(wdioConf)).isFalse();
    }

    //collectSpecFiles

    public void testCollectSpecFiles() {
        copyFilesToProject(
            "tests/wdio/nameResolution/resolveName-spec.js",
            "tests/wdio/nameResolution/resolveNameNoParentDescribe-spec.js",
            "tests/wdio/nameResolution/resolveNameNoParentDescribeName-spec.js",
            "tests/wdio/nested/anEmpty-spec.js",
            "tests/wdio/nested/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        var specFiles = new ArrayList<PsiFile>();
        var projectRoot = PsiManager.getInstance(getProject()).findDirectory(ProjectUtil.guessProjectDir(getProject()));
        TerraWdioFolders.collectSpecFiles(projectRoot, specFiles);

        var specFilePaths = specFiles.stream().map(file -> file.getVirtualFile().getPath()).toList();

        assertThat(specFilePaths).containsExactlyInAnyOrder(
            "/src/tests/wdio/nameResolution/resolveName-spec.js",
            "/src/tests/wdio/nameResolution/resolveNameNoParentDescribe-spec.js",
            "/src/tests/wdio/nameResolution/resolveNameNoParentDescribeName-spec.js",
            "/src/tests/wdio/nested/anEmpty-spec.js");
    }

    //isSnapshotsDirectory

    public void testIsSnapshotDirectory() {
        copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");
        var snapshots = PsiManager.getInstance(getProject())
            .findDirectory(TerraWdioFolders.projectWdioRoot(getProject()).findChild("__snapshots__"));

        assertThat(TerraWdioFolders.isSnapshotsDirectory(snapshots)).isTrue();
    }

    public void testIsNotSnapshotDirectory() {
        var projectRoot = PsiManager.getInstance(getProject()).findDirectory(ProjectUtil.guessProjectDir(getProject()));

        assertThat(TerraWdioFolders.isSnapshotsDirectory(projectRoot)).isFalse();
    }

    // isDiffScreenshot

    public void testIsDiffScreenshot() {
        var screenshot = copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isDiffScreenshot(screenshot, getProject())).isTrue();
    }

    public void testIsNotDiffScreenshotNotInWdioFiles() {
        var wdioConf = copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isDiffScreenshot(wdioConf, getProject())).isFalse();
    }

    public void testIsNotDiffScreenshot() {
        var screenshot = copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isDiffScreenshot(screenshot, getProject())).isFalse();
    }

    // isLatestScreenshot

    public void testIsLatestScreenshot() {
        var screenshot = copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isLatestScreenshot(screenshot, getProject())).isTrue();
    }

    public void testIsNotLatestScreenshotNotInWdioFiles() {
        var wdioConf = copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isLatestScreenshot(wdioConf, getProject())).isFalse();
    }

    public void testIsNotLatestScreenshot() {
        var screenshot = copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isLatestScreenshot(screenshot, getProject())).isFalse();
    }

    // isReferenceScreenshot

    public void testIsReferenceScreenshot() {
        var screenshot = copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        assertThat(TerraWdioFolders.isReferenceScreenshot(screenshot, getProject())).isTrue();
    }

    public void testIsNotReferenceScreenshotNotInWdioFiles() {
        var wdioConf = copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isReferenceScreenshot(wdioConf, getProject())).isFalse();
    }

    public void testIsNotReferenceScreenshot() {
        var screenshot = copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png");

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
