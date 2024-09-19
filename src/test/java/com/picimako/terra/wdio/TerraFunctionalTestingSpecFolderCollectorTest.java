//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link SpecFolderCollector}.
 */
public class TerraFunctionalTestingSpecFolderCollectorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/functestroot";
    }

    public void testCollectSpecFolders() {
        myFixture.copyFileToProject("package.json");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/clinical-lowlight-theme/en/chrome_huge/CollectScreenshots-spec/terra_screenshot.png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/clinical-lowlight-theme/en/chrome_medium/CollectScreenshots-spec/terra_screenshot.png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/terra-default-theme/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot.png");

        List<VirtualFile> filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(TerraWdioFolders.projectWdioRoot(getProject()));
        List<VirtualFile> latests = TerraResourceManager.getInstance(getProject()).specFolderCollector().collectSpecFoldersForTypeInside("latest", filesAndFoldersInWdioRoot).collect(toList());

        assertThat(latests).hasSize(1);
        assertThat(latests.getFirst().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/clinical-lowlight-theme/en/chrome_huge/CollectScreenshots-spec");
    }

    //To fix the test failure when copying package.json to the project
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
