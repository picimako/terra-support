//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.settings;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.screenshot.inlayhint.InlayType;

/**
 * Stores the application-level settings for this plugin.
 *
 * @since 0.5.0
 */
@State(
    name = "com.picimako.terra.settings.TerraApplicationState",
    storages = {@Storage("TerraSupportSettings.xml")}
)
public class TerraApplicationState implements PersistentStateComponent<TerraApplicationState> {

    /**
     * The list of relative paths for the locations of wdio test folders to recognize.
     *
     * @since 0.5.0
     */
    public List<RootPath> wdioRootPaths;

    /**
     * Whether to show a dialog for the user whether they are sure to delete the selected screenshot.
     *
     * @since 0.5.0
     */
    public boolean showConfirmationBeforeScreenshotDeletion = true;

    /**
     * Whether to show screenshot name inlay hints.
     *
     * @see com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider
     * @since 1.9.0
     */
    public String showScreenshotName = InlayType.Disabled.name();

    /**
     * Whether to show CSS selector inlay hints.
     *
     * @see com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider
     * @since 1.9.0
     */
    public String showCssSelector = InlayType.Disabled.name();

    public TerraApplicationState() {
        wdioRootPaths = new ArrayList<>();
        wdioRootPaths.add(new RootPath("test/wdio"));
        wdioRootPaths.add(new RootPath("tests/wdio"));
    }

    public static TerraApplicationState getInstance() {
        return ApplicationManager.getApplication().getService(TerraApplicationState.class);
    }

    @Override
    public @Nullable TerraApplicationState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TerraApplicationState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
