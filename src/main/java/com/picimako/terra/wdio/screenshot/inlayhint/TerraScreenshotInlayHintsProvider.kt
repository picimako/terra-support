//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.lang.Language
import com.intellij.lang.javascript.JSLanguageDialect
import com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.layout.panel
import com.intellij.util.ui.JBUI
import com.picimako.terra.resources.TerraBundle
import com.picimako.terra.wdio.TerraResourceManager
import com.picimako.terra.wdio.TerraResourceManager.isUsingTerra
import com.picimako.terra.wdio.TerraWdioPsiUtil.*
import com.picimako.terra.wdio.screenshot.inspection.GlobalTerraSelectorRetriever
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

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

    private val globalSelectorRetriever = GlobalTerraSelectorRetriever()

    companion object {
        private const val SCREENSHOT_HINT_LABEL: String = "screenshot: "
        private const val SELECTOR_HINT_LABEL: String = "selector: "
    }

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
        return object : ImmediateConfigurable {
            val screenshotInlayTypeModel = DefaultComboBoxModel(InlayType.values())
            val selectorInlayTypeModel = DefaultComboBoxModel(InlayType.values())

            override val mainCheckboxText: String
                get() = TerraBundle.inlay("option.main")

            override fun createComponent(listener: ChangeListener): JComponent {
                val panel = panel {
                    row {
                        label(TerraBundle.inlay("option.screenshot.name"))
                        val screenshotHintType = comboBox<InlayType>(
                            screenshotInlayTypeModel,
                            { settings.showScreenshotName },
                            { settings.showScreenshotName = it ?: InlayType.Disabled }
                        ).component
                        screenshotHintType.addActionListener {
                            settings.showScreenshotName = screenshotHintType.selectedItem as InlayType
                            listener.settingsChanged()
                        }
                    }
                    row {
                        label(TerraBundle.inlay("option.css.selectors"))
                        val selectorHintType = comboBox<InlayType>(
                            selectorInlayTypeModel,
                            { settings.showCssSelector },
                            { settings.showCssSelector = it ?: InlayType.Disabled }
                        ).component
                        selectorHintType.addActionListener {
                            settings.showCssSelector = selectorHintType.selectedItem as InlayType
                            listener.settingsChanged()
                        }
                    }
                }
                panel.border = JBUI.Borders.empty(2)
                return panel
            }

            override fun reset() {
                screenshotInlayTypeModel.selectedItem = settings.showScreenshotName
                selectorInlayTypeModel.selectedItem = settings.showCssSelector
            }
        }
    }

    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): InlayHintsCollector? {
        return object : FactoryInlayHintsCollector(editor) {
            override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
                if (isUsingTerra(element.project) && !file.project.service<DumbService>().isDumb && element is JSCallExpression && isScreenshotValidationCall(element)) {
                    var addedInlineScreenshot = false
                    if (settings.showScreenshotName != InlayType.Disabled) {
                        val nameExpr = getFirstArgumentAsStringLiteral(element.argumentList);
                        val nameResolver = TerraResourceManager.getInstance(element.project).screenshotNameResolver();
                        val screenshotName = nameResolver.resolveWithFallback(nameExpr, element.methodExpression);
                        when (settings.showScreenshotName) {
                            InlayType.Inline -> {
                                addInlineHint(element, SCREENSHOT_HINT_LABEL, screenshotName)
                                addedInlineScreenshot = true
                            }
                            InlayType.Block -> addBlockHint(element, SCREENSHOT_HINT_LABEL, screenshotName)
                            else -> {
                            }
                        }
                    }
                    if (settings.showCssSelector != InlayType.Disabled) {
                        val selectorProperty = getScreenshotValidationProperty(element, SELECTOR)
                        if (selectorProperty == null) {
                            val cssSelector = globalSelectorRetriever.getSelector(element.project) ?: ""
                            if (cssSelector.isNotEmpty()) {
                                when (settings.showCssSelector) {
                                    //When the screenshot name and CSS selector hints are displayed inline, an extra comma is inserted between them to separate them better visually
                                    InlayType.Inline -> addInlineHint(element, if (addedInlineScreenshot) ", $SELECTOR_HINT_LABEL" else SELECTOR_HINT_LABEL, cssSelector)
                                    InlayType.Block -> addBlockHint(element, SELECTOR_HINT_LABEL, cssSelector)
                                    else -> {
                                    }
                                }
                            }
                        }
                    }
                }
                return true
            }

            private fun addInlineHint(element: JSCallExpression, label: String, value: String) {
                sink.addInlineElement(element.parent.startOffset + element.parent.textLength, true, basePresentation(label, value, InlayType.Inline.padding))
            }

            private fun addBlockHint(element: JSCallExpression, label: String, value: String) {
                val insetPres = factory.inset(basePresentation(label, value, InlayType.Block.padding), left = calculateBlockInlayStartOffset(element))
                sink.addBlockElement(element.parent.startOffset, relatesToPrecedingText = true, showAbove = true, priority = 0, presentation = insetPres)
            }

            private fun basePresentation(label: String, value: String, padding: Int): InlayPresentation {
                return factory.container(
                    factory.seq(factory.smallText(label), factory.smallText(value)),
                    padding = InlayPresentationFactory.Padding(padding, padding, padding, padding))
            }

            private fun calculateBlockInlayStartOffset(element: JSCallExpression): Int {
                val width = EditorUtil.getPlainSpaceWidth(editor)
                val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
                val line = document!!.getLineNumber(element.parent.startOffset)
                val startOffset = document.getLineStartOffset(line)
                return (element.parent.startOffset - startOffset) * width
            }
        }
    }

    override fun createSettings(): Settings = Settings()

    override fun isLanguageSupported(language: Language): Boolean = language is JSLanguageDialect

    data class Settings(var showScreenshotName: InlayType = InlayType.Disabled, var showCssSelector: InlayType = InlayType.Disabled)

    enum class InlayType(val padding: Int = 0) {
        Disabled, Inline(2), Block(1)
    }
}
