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

package com.picimako.terra.wdio;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
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
        assertThat(latests.get(0).getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/clinical-lowlight-theme/en/chrome_huge/CollectScreenshots-spec");
    }
}
