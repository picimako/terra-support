//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import com.picimako.terra.wdio.TerraFunctionalTestingManager;
import com.picimako.terra.wdio.TerraResourceManager;

/**
 * A terra-functional-testing aware implementation of {@code BasePlatformTestCase}.
 */
public abstract class TerraFunctionalTestingTestCase extends TerraSupportTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TerraResourceManager.getInstance(getProject(), TerraFunctionalTestingManager.class);
    }
}
