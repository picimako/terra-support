//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
        component = new TerraSettingsComponent(new ArrayList<>(settings.wdioRootPaths), settings.showConfirmationBeforeScreenshotDeletion);
        return component.getSettingsPanel();
    }

    @Override
    public boolean isModified() {
        var settings = TerraApplicationState.getInstance();
        return !settings.wdioRootPaths.equals(component.getWdioRootPaths())
            || !settings.showConfirmationBeforeScreenshotDeletion == component.isScreenshotDeletionConfirmationCheckboxSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        var settings = TerraApplicationState.getInstance();
        settings.wdioRootPaths = component.getWdioRootPaths();
        settings.showConfirmationBeforeScreenshotDeletion = component.isScreenshotDeletionConfirmationCheckboxSelected();
    }

    @Override
    public void reset() {
        var settings = TerraApplicationState.getInstance();
        component.setWdioRootPaths(new ArrayList<>(settings.wdioRootPaths));
        component.setScreenshotDeletionConfirmationCheckboxSelected(settings.showConfirmationBeforeScreenshotDeletion);
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
