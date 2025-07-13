//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
import lombok.Getter;

import com.picimako.terra.resources.TerraBundle;

/**
 * Provides the UI components for this plugin's Settings panel.
 *
 * @since 0.5.0
 */
public class TerraSettingsComponent {

    @Getter
    private final JPanel settingsPanel;
    private final ListTableModel<RootPath> wdioRootPathsTableViewModel;
    private final JCheckBox screenshotDeletionConfirmationCheckbox;

    public TerraSettingsComponent(List<RootPath> wdioRootPaths, boolean isScreenshotDeletionConfirmationCheckboxSelected) {
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

        screenshotDeletionConfirmationCheckbox = new JCheckBox(TerraBundle.settings("confirm.screenshot.deletion"));
        screenshotDeletionConfirmationCheckbox.setSelected(isScreenshotDeletionConfirmationCheckboxSelected);

        settingsPanel = FormBuilder.createFormBuilder()
            .addComponent(new TitledSeparator(TerraBundle.settings("wdio.paths.section.title")))
            .addComponent(createRootPathsHelpLabel())
            .addComponent(wdioRootPathsPanel)
            .addVerticalGap(10)
            .addComponent(new TitledSeparator(TerraBundle.settings("terra.wdio.tool.window.section.title")))
            .addComponent(screenshotDeletionConfirmationCheckbox)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
    }

    private ContextHelpLabel createRootPathsHelpLabel() {
        var rootPathsHelpLabel = new ContextHelpLabel("", TerraBundle.settings("wdio.paths.note"));
        rootPathsHelpLabel.setIcon(AllIcons.General.ContextHelp);
        return rootPathsHelpLabel;
    }

    //Setters, getters

    public void setWdioRootPaths(List<RootPath> wdioRootPaths) {
        wdioRootPathsTableViewModel.setItems(wdioRootPaths);
    }

    public List<RootPath> getWdioRootPaths() {
        var rootPaths = new ArrayList<>(wdioRootPathsTableViewModel.getItems());
        rootPaths.removeIf(rootPath -> rootPath.getPath().isEmpty());
        return rootPaths;
    }

    public void setScreenshotDeletionConfirmationCheckboxSelected(boolean selected) {
        screenshotDeletionConfirmationCheckbox.setSelected(selected);
    }

    public boolean isScreenshotDeletionConfirmationCheckboxSelected() {
        return screenshotDeletionConfirmationCheckbox.isSelected();
    }
}
