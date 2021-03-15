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

package com.picimako.terra.settings;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.ListTableModel;

import com.picimako.terra.resources.TerraBundle;

/**
 * Provides the UI components for this plugin's Settings panel.
 *
 * @since 0.5.0
 */
public class TerraSettingsComponent {

    private final JPanel settingsPanel;
    private final ListTableModel<RootPath> wdioRootPathsTableViewModel;
    private final JCheckBox screenshotDeletionReminderCheckbox;

    public TerraSettingsComponent(List<RootPath> wdioRootPaths, boolean isScreenshotDeletionReminderCheckboxSelected) {
        var wdioRootPathsTableView = new TableView<>(new WdioRootPathsTableModelCreator().create());
        wdioRootPathsTableViewModel = (ListTableModel<RootPath>) wdioRootPathsTableView.getTableViewModel();
        wdioRootPathsTableViewModel.setItems(wdioRootPaths);
        JPanel wdioRootPathsPanel = ToolbarDecorator.createDecorator(wdioRootPathsTableView)
            .setAddAction(button -> {
                if (!wdioRootPathsTableViewModel.getItems().contains(RootPath.EMPTY)) {
                    wdioRootPathsTableViewModel.addRow(new RootPath());
                }
            })
            .createPanel();

        screenshotDeletionReminderCheckbox = new JCheckBox(TerraBundle.settings("confirm.screenshot.deletion"));
        screenshotDeletionReminderCheckbox.setSelected(isScreenshotDeletionReminderCheckboxSelected);

        settingsPanel = FormBuilder.createFormBuilder()
            .addComponent(new TitledSeparator(TerraBundle.settings("wdio.paths.section.title")))
            .addComponent(createRootPathsHelpLabel())
            .addComponent(wdioRootPathsPanel)
            .addVerticalGap(10)
            .addComponent(new TitledSeparator(TerraBundle.settings("terra.wdio.tool.window.section.title")))
            .addComponent(screenshotDeletionReminderCheckbox)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
    }

    private ContextHelpLabel createRootPathsHelpLabel() {
        var rootPathsHelpLabel = new ContextHelpLabel("", TerraBundle.settings("wdio.paths.note"));
        rootPathsHelpLabel.setIcon(AllIcons.General.ContextHelp);
        return rootPathsHelpLabel;
    }


    //Setters, getters

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public void setWdioRootPaths(List<RootPath> wdioRootPaths) {
        wdioRootPathsTableViewModel.setItems(wdioRootPaths);
    }

    public List<RootPath> getWdioRootPaths() {
        var rootPaths = new ArrayList<>(wdioRootPathsTableViewModel.getItems());
        rootPaths.removeIf(rootPath -> rootPath.getPath().isEmpty());
        return rootPaths;
    }

    public void setScreenshotDeletionReminderCheckboxSelected(boolean selected) {
        screenshotDeletionReminderCheckbox.setSelected(selected);
    }

    public boolean isScreenshotDeletionReminderCheckboxSelected() {
        return screenshotDeletionReminderCheckbox.isSelected();
    }
}
