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

import org.jetbrains.annotations.NotNull;

/**
 * Takes a screenshot file's path and retrieves the context information from it.
 */
public abstract class ScreenshotContextParser {

    protected static final String CONTEXT_SEPARATOR = " / ";
    protected static final String PATH_DELIMITER = "/";
    protected String contextSeparator = CONTEXT_SEPARATOR;

    public ScreenshotContextParser() {
    }

    public ScreenshotContextParser(String contextSeparator) {
        this.contextSeparator = contextSeparator;
    }

    /**
     * Parses the context information (theme, locale, browser, viewport) from the provided path.
     *
     * @param filePath the screenshot's file path to process
     * @return the context information
     */
    public abstract String parse(@NotNull String filePath);
}
