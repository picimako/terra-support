/*
 * Copyright 2020 Tamás Balog
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
     * @return the actual message
     */
    public static String inspection(@NonNls String id) {
        return message("terra.inspection." + id);
    }

    /**
     * Retrieves a Terra wdio tool window specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String toolWindow(@NonNls String id) {
        return message("terra.wdio.toolwindow." + id);
    }

    /**
     * Retrieves a Terra Settings specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String settings(@NonNls String id) {
        return message("terra.settings." + id);
    }

    /**
     * Retrieves a Terra inlay hints specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String inlay(@NonNls String id) {
        return message("terra.inlay.hints." + id);
    }
}
