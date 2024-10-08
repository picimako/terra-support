//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
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

    // projectWdioRoot

    public void testReturnVirtualFileForProjectWdioRoot() {
        myFixture.copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        VirtualFile wdioRoot = TerraWdioFolders.projectWdioRoot(getProject());

        runAssertions(wdioRoot, "/tests/wdio");
    }

    public void testReturnNoRootDirWhenProjectWdioRootDoesntExist() {
        assertThat(TerraWdioFolders.projectWdioRoot(getProject())).isNull();
    }

    // getTestRoot

    public void testReturnTestRootDirectory() {
        myFixture.copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(TerraWdioFolders.getTestRoot(getProject(), "wdio")).isNotNull();
    }

    public void testReturnNoTestRootWhenNoTestRootExists() {
        assertThat(TerraWdioFolders.getTestRoot(getProject(), "wdio")).isNull();
    }

    // wdioRootRelativePath

    public void testReturnWdioRootRelativePath() {
        myFixture.copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(TerraWdioFolders.wdioRootRelativePath(getProject())).isEqualTo("tests/wdio");
    }

    // getRelativePathToProjectDir

    public void testReturnRelativePath() {
        myFixture.copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(TerraWdioFolders.getRelativePathToProjectDir(getProject(), TerraWdioFolders.projectWdioRoot(getProject()))).isEqualTo("tests/wdio");
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
        VirtualFile latest = FilenameIndex.getVirtualFilesByName("testimage[default].png", GlobalSearchScope.projectScope(getProject()))
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
        VirtualFile latest = FilenameIndex.getVirtualFilesByName("testimage[default].png", GlobalSearchScope.projectScope(getProject()))
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

    // specFileIdentifier

    public void testSpecFileIdentifierFolderNoNestedFolder() {
        VirtualFile specFile = myFixture.copyFileToProject("tests/wdio/FindUnusedScreenshot-spec.js");

        String id = TerraWdioFolders.specFileIdentifier(specFile, getProject());

        assertThat(id).isEqualTo("FindUnusedScreenshot-spec");
    }

    public void testSpecFileIdentifierFolderOneLevelNestedFolder() {
        VirtualFile specFile = myFixture.copyFileToProject("tests/wdio/nested/anEmpty-spec.js");

        String id = TerraWdioFolders.specFileIdentifier(specFile, getProject());

        assertThat(id).isEqualTo("nested/anEmpty-spec");
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

    //isInSnapshotsDirectory

    public void testInSnapshots() {
        VirtualFile screenshot = myFixture.copyFileToProject(reference("/en/chrome_huge/NavigateToScreenshotUsage-spec/terra_screenshot[default].png"));

        assertThat(TerraWdioFolders.isInSnapshotsDirectory(screenshot)).isTrue();
    }

    public void testNotInSnapshotsFilesForNoFile() {
        assertThat(TerraWdioFolders.isInSnapshotsDirectory(null)).isFalse();
    }

    public void testNotInSnapshots() {
        VirtualFile wdioConf = myFixture.copyFileToProject("wdio.conf.js");

        assertThat(TerraWdioFolders.isInSnapshotsDirectory(wdioConf)).isFalse();
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
