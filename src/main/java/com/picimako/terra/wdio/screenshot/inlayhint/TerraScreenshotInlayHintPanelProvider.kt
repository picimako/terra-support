//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.picimako.terra.resources.TerraBundle
import com.picimako.terra.settings.TerraApplicationState
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

/**
 * Kept as Kotlin code, so that assembling the UI panel is easier with the UI DSL.
 */
@Suppress("UnstableApiUsage")
class TerraScreenshotInlayHintPanelProvider {
    companion object {
        @JvmStatic
        fun createConfigurable(): ImmediateConfigurable {
            return object : ImmediateConfigurable {
                val screenshotInlayTypeModel =
                    DefaultComboBoxModel(InlayType.values())
                val selectorInlayTypeModel = DefaultComboBoxModel(InlayType.values())

                override val mainCheckboxText: String
                    get() = TerraBundle.inlay("option.main")

                override fun createComponent(listener: ChangeListener): JComponent {
                    val panel = panel {
                        row(TerraBundle.inlay("option.screenshot.name")) {
                            val screenshotHintType = comboBox<InlayType>(
                                screenshotInlayTypeModel,
                                SimpleListCellRenderer.create("") { it.name }
                            ).component

                            screenshotHintType.addActionListener {
                                TerraApplicationState.getInstance().showScreenshotName = (screenshotHintType.selectedItem as InlayType).name
                                listener.settingsChanged()
                            }
                        }
                        row(TerraBundle.inlay("option.css.selectors")) {
                            val selectorHintType = comboBox<InlayType>(
                                selectorInlayTypeModel,
                                SimpleListCellRenderer.create("") { it.name }
                            ).component

                            selectorHintType.addActionListener {
                                TerraApplicationState.getInstance().showCssSelector = (selectorHintType.selectedItem as InlayType).name
                                listener.settingsChanged()
                            }
                        }
                    }
                    panel.border = JBUI.Borders.empty(2)
                    return panel
                }

                override fun reset() {
                    screenshotInlayTypeModel.selectedItem = InlayType.valueOf(TerraApplicationState.getInstance().showScreenshotName)
                    selectorInlayTypeModel.selectedItem = InlayType.valueOf(TerraApplicationState.getInstance().showCssSelector)
                }
            }
        }
    }
}
