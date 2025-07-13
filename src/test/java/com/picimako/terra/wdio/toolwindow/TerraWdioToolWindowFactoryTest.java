//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.LightProjectDescriptor;

import com.picimako.terra.TerraSupportTestBase;
import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraToolkitManager;
import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Unit test for {@link TerraWdioToolWindowFactory}.
 */
public class TerraWdioToolWindowFactoryTest extends TerraSupportTestBase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testNotAvailableWhenNotUsingTerra() {
        assertThat(new TerraWdioToolWindowFactory().shouldBeAvailable(getProject())).isFalse();
    }

    public void testAvailableAndShouldConfigureWdioTestRootPath() {
        TerraResourceManager.getInstance(getProject(), TerraToolkitManager.class);
        copyFileToProject("tests/wdio/CollectScreenshots-spec.js");

        assertThat(new TerraWdioToolWindowFactory().shouldBeAvailable(getProject())).isTrue();
        assertThat(TerraWdioFolders.getWdioTestRootPath()).isEqualTo("/src/tests/wdio");
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
