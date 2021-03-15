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
import javax.swing.*;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;

/**
 * Configurable object acting as a bridge between the application-level settings and the Settings UI components.
 *
 * @since 0.5.0
 */
public class TerraSettingsConfigurable implements Configurable {

    private TerraSettingsComponent component;

    @Override
    public String getDisplayName() {
        return TerraBundle.settings("tab.title");
    }

    @Override
    public @Nullable JComponent createComponent() {
        var settings = TerraApplicationState.getInstance();
        component = new TerraSettingsComponent(new ArrayList<>(settings.wdioRootPaths), settings.isShowReminderBeforeScreenshotDeletion);
        return component.getSettingsPanel();
    }

    @Override
    public boolean isModified() {
        var settings = TerraApplicationState.getInstance();
        return !settings.wdioRootPaths.equals(component.getWdioRootPaths())
            || !settings.isShowReminderBeforeScreenshotDeletion == component.isScreenshotDeletionReminderCheckboxSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        var settings = TerraApplicationState.getInstance();
        settings.wdioRootPaths = component.getWdioRootPaths();
        settings.isShowReminderBeforeScreenshotDeletion = component.isScreenshotDeletionReminderCheckboxSelected();
    }

    @Override
    public void reset() {
        var settings = TerraApplicationState.getInstance();
        component.setWdioRootPaths(new ArrayList<>(settings.wdioRootPaths));
        component.setScreenshotDeletionReminderCheckboxSelected(settings.isShowReminderBeforeScreenshotDeletion);
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
