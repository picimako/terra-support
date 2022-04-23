//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the project-level properties for screenshot statistics in the Terra wdio tool window.
 *
 * @since 0.5.0
 */
@State(
    name = "com.picimako.terra.wdio.toolwindow.ScreenshotStatisticsProjectService",
    storages = {@Storage("TerraWdioScreenshotStatistics.xml")}
)
@Service //Service.Level.PROJECT
public final class ScreenshotStatisticsProjectService implements PersistentStateComponent<ScreenshotStatisticsProjectService> {

    public boolean isShowStatistics = true;

    public static ScreenshotStatisticsProjectService getInstance(Project project) {
        return project.getService(ScreenshotStatisticsProjectService.class);
    }

    @Override
    public @Nullable ScreenshotStatisticsProjectService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ScreenshotStatisticsProjectService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
