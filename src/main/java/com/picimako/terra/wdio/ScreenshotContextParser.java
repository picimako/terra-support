//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import org.jetbrains.annotations.NotNull;

/**
 * Takes a screenshot file's path and retrieves the context information from it.
 */
public abstract class ScreenshotContextParser {

    protected static final String CONTEXT_SEPARATOR = " / ";
    protected static final String PATH_DELIMITER = "/";
    protected String contextSeparator = CONTEXT_SEPARATOR;

    protected ScreenshotContextParser() {
    }

    protected ScreenshotContextParser(String contextSeparator) {
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
