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

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.testFramework.utils.inlays.InlayHintsProviderTestCase
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
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showCssSelector = Inline, showScreenshotName = Inline))
    }

    private fun doTest(
        text: String,
        settings: TerraScreenshotInlayHintsProvider.Settings = TerraScreenshotInlayHintsProvider.Settings()
    ) {
        testProvider(
            "test.js",
            text,
            TerraScreenshotInlayHintsProvider(),
            settings
        )
    }
}
