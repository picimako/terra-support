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
