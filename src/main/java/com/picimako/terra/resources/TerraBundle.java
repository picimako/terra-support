//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.resources;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Resource bundle for all messages in this plugin.
 */
public class TerraBundle extends DynamicBundle {

    @NonNls
    private static final String TERRA_BUNDLE = "messages.TerraBundle";
    private static final TerraBundle INSTANCE = new TerraBundle();

    private TerraBundle() {
        super(TERRA_BUNDLE);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = TERRA_BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

    /**
     * Retrieves an inspection specific message for the provided id.
     *
     * @param id the suffix of the message key
     */
    public static String inspection(@NonNls String id, Object @NotNull ... params) {
        return message("terra.inspection." + id, params);
    }

    /**
     * Retrieves a Terra wdio tool window specific message for the provided id.
     *
     * @param id the suffix of the message key
     */
    public static String toolWindow(@NonNls String id, Object @NotNull ... params) {
        return message("terra.wdio.toolwindow." + id, params);
    }

    /**
     * Retrieves a Terra Settings specific message for the provided id.
     *
     * @param id the suffix of the message key
     */
    public static String settings(@NonNls String id) {
        return message("terra.settings." + id);
    }

    /**
     * Retrieves a Terra inlay hints specific message for the provided id.
     *
     * @param id the suffix of the message key
     */
    public static String inlay(@NonNls String id) {
        return message("terra.inlay.hints." + id);
    }
}
