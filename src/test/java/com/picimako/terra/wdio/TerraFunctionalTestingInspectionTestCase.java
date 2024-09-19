//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import com.intellij.testFramework.LightProjectDescriptor;

/**
 * A terra-functional-testing aware implementation of {@code TerraInspectionBaseTestCase}.
 */
public abstract class TerraFunctionalTestingInspectionTestCase extends TerraInspectionBaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TerraResourceManager.getInstance(getProject(), TerraFunctionalTestingManager.class);
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
