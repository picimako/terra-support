//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link GlobalTerraSelectorInspection}.
 */
public class GlobalTerraSelectorInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot/";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new GlobalTerraSelectorInspection();
    }

    public void testGlobalSelector() {
        copyFileToProject("wdio.conf.js");
        doWdioSpecTest("globalTerraSelectorSpecs/");
    }

    public void testGlobalSelectorNoWdioConfig() {
        doWdioSpecTest("globalTerraSelectorSpecs/");
    }
}
