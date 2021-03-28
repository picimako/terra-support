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
import com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider.InlayType.Block
import com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider.InlayType.Inline

/**
 * Unit test for [TerraScreenshotInlayHintsProvider].
 */
class TerraScreenshotInlayHintsProviderTest : InlayHintsProviderTestCase() {

    override fun getTestDataPath(): String {
        return "testdata/terra/projectroot"
    }

    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("wdio.conf.js")
    }

    //No hint

    fun testNoInlayHint() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot('test id', { selector: '#selector' });
        Terra.validates.element('test id', { selector: '#selector' });
    });
});""".trimIndent())
    }

    //Single hint

    fun testOnlyScreenshotNameBlock() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
<# block [screenshot:  Terra_screenshot[test_id].png] #>
        Terra.validates.screenshot('test id');
<# block [screenshot:  Terra_screenshot[test_id].png] #>
        Terra.validates.element('test id');
    });
<# block [screenshot:  Terra_screenshot[test_id].png] #>
    Terra.it.matchesScreenshot('test id');
<# block [screenshot:  Terra_screenshot[test_id].png] #>
    Terra.it.validatesElement('test id');
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showScreenshotName = Block))
    }

    fun testOnlyScreenshotNameInline() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
        Terra.validates.element('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
    });
    Terra.it.matchesScreenshot('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
    Terra.it.validatesElement('test id');<# [screenshot:  Terra_screenshot[test_id].png] #>
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showScreenshotName = Inline))
    }

    fun testOnlySelectorBlock() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
<# block [selector:  .second-se'l'ector] #>
        Terra.validates.screenshot('test id');
<# block [selector:  .second-se'l'ector] #>
        Terra.validates.element('test id');
    });
<# block [selector:  .second-se'l'ector] #>
    Terra.it.matchesScreenshot('test id');
<# block [selector:  .second-se'l'ector] #>
    Terra.it.validatesElement('test id');
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showCssSelector = Block))
    }

    fun testOnlySelectorInline() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot('test id');<# [selector:  .second-se'l'ector] #>
        Terra.validates.element('test id');<# [selector:  .second-se'l'ector] #>
    });
    Terra.it.matchesScreenshot('test id');<# [selector:  .second-se'l'ector] #>
    Terra.it.validatesElement('test id');<# [selector:  .second-se'l'ector] #>
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showCssSelector = Inline))
    }

    //All hints

    fun testWithTestIdAndSelector() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot('test id', { selector: '#selector' });<# [screenshot:  Terra_screenshot[test_id].png] #>
        Terra.validates.element('test id', { selector: '#selector' });<# [screenshot:  Terra_screenshot[test_id].png] #>
    });
    Terra.it.matchesScreenshot('test id', { selector: '#selector' });<# [screenshot:  Terra_screenshot[test_id].png] #>
    Terra.it.validatesElement('test id', { selector: '#selector' });<# [screenshot:  Terra_screenshot[test_id].png] #>
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showCssSelector = Inline, showScreenshotName = Inline))
    }

    fun testWithTestId() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
<# block [screenshot:  Terra_screenshot[test_id].png]
[selector:  .second-se'l'ector] #>
        Terra.validates.screenshot('test id');
<# block [screenshot:  Terra_screenshot[test_id].png]
[selector:  .second-se'l'ector] #>
        Terra.validates.element('test id');
    });
<# block [screenshot:  Terra_screenshot[test_id].png]
[selector:  .second-se'l'ector] #>
    Terra.it.matchesScreenshot('test id');
<# block [screenshot:  Terra_screenshot[test_id].png]
[selector:  .second-se'l'ector] #>
    Terra.it.validatesElement('test id');
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showCssSelector = Block, showScreenshotName = Block))
    }

    fun testWithSelector() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
<# block [screenshot:  Terra_screenshot[default].png] #>
        Terra.validates.screenshot({ selector: '#selector' });
<# block [screenshot:  Terra_screenshot[default].png] #>
        Terra.validates.element({ selector: '#selector' });
    });
<# block [screenshot:  Terra_screenshot[default].png] #>
    Terra.it.matchesScreenshot({ selector: '#selector' });
<# block [screenshot:  Terra_screenshot[default].png] #>
    Terra.it.validatesElement({ selector: '#selector' });
});""".trimIndent(), TerraScreenshotInlayHintsProvider.Settings(showCssSelector = Block, showScreenshotName = Block))
    }

    fun testWithoutTestIdAndSelector() {
        doTest("""
describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot();<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
        Terra.validates.element();<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
    });
    Terra.it.matchesScreenshot();<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
    Terra.it.validatesElement();<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
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
