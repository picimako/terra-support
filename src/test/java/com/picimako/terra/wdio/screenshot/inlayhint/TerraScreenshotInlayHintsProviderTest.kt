//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.testFramework.utils.inlays.InlayHintsProviderTestCase
import com.picimako.terra.settings.TerraApplicationState
import com.picimako.terra.wdio.TerraResourceManager
import com.picimako.terra.wdio.TerraToolkitManager

/**
 * Unit test for [TerraScreenshotInlayHintsProvider].
 */
class TerraScreenshotInlayHintsProviderTest : InlayHintsProviderTestCase() {

    override fun getTestDataPath(): String {
        return "testdata/terra/projectroot"
    }

    override fun setUp() {
        super.setUp()
        TerraResourceManager.getInstance(project, TerraToolkitManager::class.java)
        myFixture.copyFileToProject("wdio.conf.js")
    }

    //No hint

    fun testNoInlayHint() {
        TerraApplicationState.getInstance().showScreenshotName = "Disabled"
        TerraApplicationState.getInstance().showCssSelector = "Disabled"
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
        TerraApplicationState.getInstance().showScreenshotName = "Block"
        TerraApplicationState.getInstance().showCssSelector = "Disabled"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
            <# block [screenshot:  Terra_screenshot[test_id].png] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png] #>*/
                    Terra.validates.screenshot('test id');
            <# block [screenshot:  Terra_screenshot[test_id].png] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png] #>*/
                    Terra.validates.element('test id');
                });
            <# block [screenshot:  Terra_screenshot[test_id].png] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png] #>*/
                Terra.it.matchesScreenshot('test id');
            <# block [screenshot:  Terra_screenshot[test_id].png] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png] #>*/
                Terra.it.validatesElement('test id');
            });""".trimIndent())
    }

    fun testOnlyScreenshotNameInline() {
        TerraApplicationState.getInstance().showScreenshotName = "Inline"
        TerraApplicationState.getInstance().showCssSelector = "Disabled"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
                    Terra.validates.screenshot('test id');/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
                    Terra.validates.element('test id');/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
                });
                Terra.it.matchesScreenshot('test id');/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
                Terra.it.validatesElement('test id');/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
            });""".trimIndent())
    }

    fun testOnlySelectorBlock() {
        TerraApplicationState.getInstance().showScreenshotName = "Disabled"
        TerraApplicationState.getInstance().showCssSelector = "Block"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
            <# block [selector:  .second-se'l'ector] #>
            /*<# block [selector:  .second-se'l'ector] #>*/
                    Terra.validates.screenshot('test id');
            <# block [selector:  .second-se'l'ector] #>
            /*<# block [selector:  .second-se'l'ector] #>*/
                    Terra.validates.element('test id');
                });
            <# block [selector:  .second-se'l'ector] #>
            /*<# block [selector:  .second-se'l'ector] #>*/
                Terra.it.matchesScreenshot('test id');
            <# block [selector:  .second-se'l'ector] #>
            /*<# block [selector:  .second-se'l'ector] #>*/
                Terra.it.validatesElement('test id');
            });""".trimIndent())
    }

    fun testOnlySelectorInline() {
        TerraApplicationState.getInstance().showScreenshotName = "Disabled"
        TerraApplicationState.getInstance().showCssSelector = "Inline"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
                    Terra.validates.screenshot('test id');/*<# [selector:  .second-se'l'ector] #>*/<# [selector:  .second-se'l'ector] #>
                    Terra.validates.element('test id');/*<# [selector:  .second-se'l'ector] #>*/<# [selector:  .second-se'l'ector] #>
                });
                Terra.it.matchesScreenshot('test id');/*<# [selector:  .second-se'l'ector] #>*/<# [selector:  .second-se'l'ector] #>
                Terra.it.validatesElement('test id');/*<# [selector:  .second-se'l'ector] #>*/<# [selector:  .second-se'l'ector] #>
            });""".trimIndent())
    }

    //All hints

    fun testWithTestIdAndSelector() {
        TerraApplicationState.getInstance().showScreenshotName = "Inline"
        TerraApplicationState.getInstance().showCssSelector = "Inline"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
                    Terra.validates.screenshot('test id', { selector: '#selector' });/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
                    Terra.validates.element('test id', { selector: '#selector' });/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
                });
                Terra.it.matchesScreenshot('test id', { selector: '#selector' });/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
                Terra.it.validatesElement('test id', { selector: '#selector' });/*<# [screenshot:  Terra_screenshot[test_id].png] #>*/<# [screenshot:  Terra_screenshot[test_id].png] #>
            });""".trimIndent())
    }

    fun testWithTestId() {
        TerraApplicationState.getInstance().showScreenshotName = "Block"
        TerraApplicationState.getInstance().showCssSelector = "Block"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
            <# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>*/
                    Terra.validates.screenshot('test id');
            <# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>*/
                    Terra.validates.element('test id');
                });
            <# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>*/
                Terra.it.matchesScreenshot('test id');
            <# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>
            /*<# block [screenshot:  Terra_screenshot[test_id].png]
            [selector:  .second-se'l'ector] #>*/
                Terra.it.validatesElement('test id');
            });""".trimIndent())
    }

    fun testWithSelector() {
        TerraApplicationState.getInstance().showScreenshotName = "Block"
        TerraApplicationState.getInstance().showCssSelector = "Block"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
            <# block [screenshot:  Terra_screenshot[default].png] #>
            /*<# block [screenshot:  Terra_screenshot[default].png] #>*/
                    Terra.validates.screenshot({ selector: '#selector' });
            <# block [screenshot:  Terra_screenshot[default].png] #>
            /*<# block [screenshot:  Terra_screenshot[default].png] #>*/
                    Terra.validates.element({ selector: '#selector' });
                });
            <# block [screenshot:  Terra_screenshot[default].png] #>
            /*<# block [screenshot:  Terra_screenshot[default].png] #>*/
                Terra.it.matchesScreenshot({ selector: '#selector' });
            <# block [screenshot:  Terra_screenshot[default].png] #>
            /*<# block [screenshot:  Terra_screenshot[default].png] #>*/
                Terra.it.validatesElement({ selector: '#selector' });
            });""".trimIndent())
    }

    fun testWithoutTestIdAndSelector() {
        TerraApplicationState.getInstance().showScreenshotName = "Inline"
        TerraApplicationState.getInstance().showCssSelector = "Inline"
        doTest("""
            describe('Terra screenshot', () => {
                it('Test case', () => {
                    Terra.validates.screenshot();/*<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>*/<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
                    Terra.validates.element();/*<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>*/<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
                });
                Terra.it.matchesScreenshot();/*<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>*/<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
                Terra.it.validatesElement();/*<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>*/<# [[screenshot:  Terra_screenshot[default].png] [, selector:  .second-se'l'ector]] #>
            });""".trimIndent())
    }

    private fun doTest(text: String) {
        doTestProvider("test.js", text, TerraScreenshotInlayHintsProvider(), TerraScreenshotInlayHintsProvider.Settings())
    }
}
