//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
