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
