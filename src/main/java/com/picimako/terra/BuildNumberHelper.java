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
