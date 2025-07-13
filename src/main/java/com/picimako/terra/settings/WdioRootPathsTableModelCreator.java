//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.settings;

import javax.swing.table.TableCellEditor;

import com.intellij.execution.util.StringWithNewLinesCellEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;

/**
 * Creates the {@link ListTableModel} for the Wdio root paths part of the Settings dialog.
 */
public final class WdioRootPathsTableModelCreator {

    public ListTableModel<RootPath> create() {
        final ColumnInfo<RootPath, String> wdioRootPaths = new ColumnInfo<>(TerraBundle.settings("wdio.paths.column.title")) {
            @Override
            public @Nullable String valueOf(RootPath path) {
                return path.getPath();
            }

            @Override
            public void setValue(RootPath path, String value) {
                if (!value.equals(valueOf(path))) {
                    path.setPath(value);
                }
            }

            @Override
            public boolean isCellEditable(RootPath path) {
                return true;
            }

            @NotNull
            @Override
            public TableCellEditor getEditor(RootPath path) {
                return new StringWithNewLinesCellEditor();
            }
        };

        return new ListTableModel<>(wdioRootPaths);
    }
}
