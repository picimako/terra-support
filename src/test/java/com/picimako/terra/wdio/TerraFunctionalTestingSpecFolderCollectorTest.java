//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.testFramework.LightProjectDescriptor;

import com.picimako.terra.TerraSupportTestBase;

/**
 * Unit test for {@link SpecFolderCollector}.
 */
public class TerraFunctionalTestingSpecFolderCollectorTest extends TerraSupportTestBase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/functestroot";
    }

    public void testCollectSpecFolders() {
        copyFilesToProject(
            "package.json",
            "tests/wdio/__snapshots__/latest/clinical-lowlight-theme/en/chrome_huge/CollectScreenshots-spec/terra_screenshot.png",
            "tests/wdio/__snapshots__/reference/clinical-lowlight-theme/en/chrome_medium/CollectScreenshots-spec/terra_screenshot.png",
            "tests/wdio/__snapshots__/reference/terra-default-theme/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot.png");

        var filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(TerraWdioFolders.projectWdioRoot(getProject()));
        var latests = TerraResourceManager.getInstance(getProject()).specFolderCollector().collectSpecFoldersForTypeInside("latest", filesAndFoldersInWdioRoot).toList();

        assertThat(latests).hasSize(1);
        assertThat(latests.getFirst().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/clinical-lowlight-theme/en/chrome_huge/CollectScreenshots-spec");
    }

    //To fix the test failure when copying package.json to the project
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
