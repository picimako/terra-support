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

package com.picimako.terra.wdio;

import com.intellij.openapi.ui.Messages;

import com.picimako.terra.resources.TerraBundle;

/**
 * Provides various dialogs that would be displayed to users.
 */
public final class ProblemDialogs {

    public static void showNoValidationCallToNavigateToDialog() {
        Messages.showWarningDialog(
            TerraBundle.toolWindow("screenshot.navigate.to.usage.no.validation.call.message"),
            TerraBundle.toolWindow("screenshot.navigate.to.usage.no.validation.call.title"));
    }

    public static void showNoSpecFileToNavigateToDialog() {
        Messages.showWarningDialog(
            TerraBundle.toolWindow("screenshot.navigate.to.usage.no.spec.file.message"),
            TerraBundle.toolWindow("screenshot.navigate.to.usage.no.spec.file.title"));
    }

    private ProblemDialogs() {
        //Utility class
    }
}
