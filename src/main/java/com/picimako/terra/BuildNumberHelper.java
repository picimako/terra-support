//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.BuildNumber;

/**
 * Helper for IDE build numbers.
 * <p>
 * It is useful when needing to validate whether a certain feature can or cannot be used in the currently run IDE version.
 */
public final class BuildNumberHelper {

    /**
     * Gets whether the currently used IDE's build number is the same or newer than the one in the argument.
     *
     * @param buildNumber the build number to compare to
     */
    public static boolean isIDEBuildNumberSameOrNewerThan(String buildNumber) {
        return ApplicationInfo.getInstance().getBuild().compareTo(requireNonNull(BuildNumber.fromString(buildNumber))) >= 0;
    }

    private BuildNumberHelper() {
    }
}
