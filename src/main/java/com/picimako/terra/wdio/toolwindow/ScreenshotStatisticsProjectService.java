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
