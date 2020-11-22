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

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.TerraWdioFolders.DIFF_RELATIVE_PATH;
import static com.picimako.terra.wdio.TerraWdioFolders.REFERENCE_RELATIVE_PATH;

import org.jetbrains.annotations.NotNull;

/**
 * A screenshot context includes the locale, the browser and viewport a particular screenshot is created for.
 */
public final class ScreenshotContextParser {
    public static final String CONTEXT_SEPARATOR = " | ";
    public static final String PATH_DELIMITER = "/";

    /**
     * Parses the argument path and returns the locale, browser and viewport parts from it in the following format: {@code locale | browser | viewport}.
     * <p>
     * For example: {@code en | chrome | huge} from the partial string {@code /en/chrome_huge}.
     * <p>
     * It handles the diff and reference paths, but not a latest path because this method is not supposed to be called
     * with the latest path of an image.
     *
     * @param virtualFilePath the path to parse
     * @return the formatted context string
     */
    public String parse(@NotNull String virtualFilePath) {
        String relativePath = virtualFilePath.contains(DIFF_RELATIVE_PATH) ? DIFF_RELATIVE_PATH : REFERENCE_RELATIVE_PATH;
        String relativeToSnapshots = virtualFilePath.substring(virtualFilePath.lastIndexOf(relativePath) + relativePath.length() + 1);
        String[] split = relativeToSnapshots.split(PATH_DELIMITER);
        return split[0] + CONTEXT_SEPARATOR + split[1].replace("_", CONTEXT_SEPARATOR);
    }
}
