//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.vfs.VfsUtil;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link SpecFolderCollector}.
 */
public class TerraToolkitSpecFolderCollectorTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testCollectSpecFolders() {
        copyFilesToProject(
            "tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/testimage[default].png",
            "tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/testimage[default].png",
            "tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/testimage[default].png");

        var filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(TerraWdioFolders.projectWdioRoot(getProject()));
        var diffs = TerraResourceManager.getInstance(getProject()).specFolderCollector()
            .collectSpecFoldersForTypeInside("diff", filesAndFoldersInWdioRoot)
            .toList();

        assertThat(diffs).hasSize(1);
        assertThat(diffs.getFirst().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec");
    }
}
