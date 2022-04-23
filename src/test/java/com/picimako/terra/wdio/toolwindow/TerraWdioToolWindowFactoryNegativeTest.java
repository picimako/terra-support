//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraToolkitManager;

/**
 * Unit test for {@link TerraWdioToolWindowFactory}.
 */
public class TerraWdioToolWindowFactoryNegativeTest extends BasePlatformTestCase {

    public void testNotAvailableForNonExistentWdioRoot() {
        TerraResourceManager.getInstance(getProject(), TerraToolkitManager.class);

        assertThat(new TerraWdioToolWindowFactory().shouldBeAvailable(getProject())).isFalse();
    }
}
