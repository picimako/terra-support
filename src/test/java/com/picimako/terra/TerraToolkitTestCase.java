//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraToolkitManager;

/**
 * A terra-toolkit aware implementation of {@code BasePlatformTestCase}.
 */
public abstract class TerraToolkitTestCase extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TerraResourceManager.getInstance(getProject(), TerraToolkitManager.class);
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
