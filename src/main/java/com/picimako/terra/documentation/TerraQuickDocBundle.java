//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.documentation;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Resource bundle for quick documentation related messages.
 *
 * @since 0.6.0
 */
public class TerraQuickDocBundle extends DynamicBundle {

    @NonNls
    private static final String TERRA_QUICK_DOC_BUNDLE = "messages.TerraQuickDocBundle";
    private static final TerraQuickDocBundle INSTANCE = new TerraQuickDocBundle();

    private TerraQuickDocBundle() {
        super(TERRA_QUICK_DOC_BUNDLE);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = TERRA_QUICK_DOC_BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
