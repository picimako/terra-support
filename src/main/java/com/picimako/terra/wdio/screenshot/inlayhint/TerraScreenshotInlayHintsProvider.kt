//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.lang.Language
import com.intellij.lang.javascript.JSLanguageDialect
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.picimako.terra.resources.TerraBundle

/**
 * Provides inlay hints for Terra screenshot validation calls.
 *
 * Hints are provided for:
 * - Screenshot names: displays the name of the referenced screenshot regardless of the presence of the call's name
 * parameter
 * - CSS selectors: displays the used global CSS selector (from wdio.conf.js) in case the `selector` property is not defined.
 *
 * Both hints can be customized in Settings under `Editor > Inlay Hints > JavaScript > Terra Screenshot`. Each of them
 * can be enabled/disabled separately, and each of them can be displayed inline (at the end of the function call's line),
 * or as block (above the function call).
 *
 * They can also be disabled at once by a common checkbox option for them.
 *
 * ### Notes
 *
 * - This class uses classes and functions from packages marked as experimental, so they may change or be unstable in
 * some circumstances.
 * - when showing the screenshot name hint, it is displayed regardless of the screenshot exists or not
 *
 * @since 0.5.0
 */
@Suppress("UnstableApiUsage")
class TerraScreenshotInlayHintsProvider : InlayHintsProvider<TerraScreenshotInlayHintsProvider.Settings> {
    override val key: SettingsKey<Settings>
        get() = SettingsKey("terra.screenshot.validation")

    override val name: String
        get() = TerraBundle.inlay("type.title")

    override val previewText: String?
        get() =
            """describe('Terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.screenshot('test id', { selector: '#selector' });
        Terra.validates.screenshot('test id');
        Terra.validates.screenshot({ selector: '#selector' });
        Terra.validates.screenshot();
    });
});"""

    override fun createConfigurable(settings: Settings): ImmediateConfigurable {
        return TerraScreenshotInlayHintPanelProvider.createConfigurable()
    }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: Settings,
        sink: InlayHintsSink
    ): InlayHintsCollector? {
        return InlayHintsCollectorProvider.getCollectorFor(file, editor, settings, sink)
    }

    override fun createSettings(): Settings = Settings()

    override fun isLanguageSupported(language: Language): Boolean = language is JSLanguageDialect

    class Settings
}
