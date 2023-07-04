//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.picimako.terra.resources.TerraBundle
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

/**
 * Kept as Kotlin code, so that assembling the UI panel is easier with the UI DSL.
 */
class TerraScreenshotInlayHintPanelProvider {
    companion object {
        @JvmStatic
        fun createConfigurable(settings: TerraScreenshotInlayHintsProvider.Settings): ImmediateConfigurable {
            return object : ImmediateConfigurable {
                val screenshotInlayTypeModel =
                    DefaultComboBoxModel(TerraScreenshotInlayHintsProvider.InlayType.values())
                val selectorInlayTypeModel = DefaultComboBoxModel(TerraScreenshotInlayHintsProvider.InlayType.values())

                override val mainCheckboxText: String
                    get() = TerraBundle.inlay("option.main")

                override fun createComponent(listener: ChangeListener): JComponent {
                    val panel = panel {
                        row(TerraBundle.inlay("option.screenshot.name")) {
                            val screenshotHintType = comboBox<TerraScreenshotInlayHintsProvider.InlayType>(
                                screenshotInlayTypeModel,
                                SimpleListCellRenderer.create("") { it.name }
                            ).component

                            screenshotHintType.addActionListener {
                                settings.showScreenshotName =
                                    screenshotHintType.selectedItem as TerraScreenshotInlayHintsProvider.InlayType
                                listener.settingsChanged()
                            }
                        }
                        row(TerraBundle.inlay("option.css.selectors")) {
                            val selectorHintType = comboBox<TerraScreenshotInlayHintsProvider.InlayType>(
                                selectorInlayTypeModel,
                                SimpleListCellRenderer.create("") { it.name }
                            ).component

                            selectorHintType.addActionListener {
                                settings.showCssSelector =
                                    selectorHintType.selectedItem as TerraScreenshotInlayHintsProvider.InlayType
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
    }
}
