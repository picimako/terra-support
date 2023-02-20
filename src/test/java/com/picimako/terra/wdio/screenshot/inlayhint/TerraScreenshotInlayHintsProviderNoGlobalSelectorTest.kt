//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.testFramework.utils.inlays.InlayHintsProviderTestCase
import com.picimako.terra.wdio.TerraResourceManager
import com.picimako.terra.wdio.TerraToolkitManager
import com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider.InlayType.Inline

/**
 * Unit test for [TerraScreenshotInlayHintsProvider].
 */
class TerraScreenshotInlayHintsProviderNoGlobalSelectorTest : InlayHintsProviderTestCase() {

    override fun getTestDataPath(): String {
        return "testdata/terra/projectroot2"
    }

    override fun setUp() {
        super.setUp()
        TerraResourceManager.getInstance(project, TerraToolkitManager::class.java)
        myFixture.copyFileToProject("wdio.conf.js")
    }

    fun testWithTestIdWithoutGlobalSelector() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
        Terra.validates.element('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
    });
    Terra.it.matchesScreenshot('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
    Terra.it.validatesElement('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(Inline, Inline))
    }

    private fun doTest(
        text: String,
        settings: TerraScreenshotInlayHintsProvider.Settings = TerraScreenshotInlayHintsProvider.Settings(
            TerraScreenshotInlayHintsProvider.InlayType.Disabled, TerraScreenshotInlayHintsProvider.InlayType.Disabled)
    ) {
        testProvider(
            "test.js",
            text,
            TerraScreenshotInlayHintsProvider(),
            settings
        )
    }
}
