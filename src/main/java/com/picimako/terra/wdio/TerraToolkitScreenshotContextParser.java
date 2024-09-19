//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.TerraWdioFolders.DIFF_RELATIVE_PATH;
import static com.picimako.terra.wdio.TerraWdioFolders.REFERENCE_RELATIVE_PATH;

import org.jetbrains.annotations.NotNull;

/**
 * Terra-toolkit specific context parser.
 * <p>
 * The screenshot context includes the locale, browser and viewport a particular screenshot is created for.
 *
 * @see TerraToolkitManager
 */
public final class TerraToolkitScreenshotContextParser extends ScreenshotContextParser {
    public static final TerraToolkitScreenshotContextParser INSTANCE = new TerraToolkitScreenshotContextParser();

    private TerraToolkitScreenshotContextParser() {
    }

    public TerraToolkitScreenshotContextParser(String contextSeparator) {
        super(contextSeparator);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the locale, browser and viewport parts from it in the following format: {@code locale | browser | viewport}.
     * <p>
     * For example: {@code en | chrome | huge} from the partial string {@code /en/chrome_huge}.
     * <p>
     * It handles the diff and reference paths, but not a latest path because this method is not supposed to be called
     * with the latest path of an image.
     */
    @Override
    public String parse(@NotNull String filePath) {
        String relativePath = filePath.contains(DIFF_RELATIVE_PATH) ? DIFF_RELATIVE_PATH : REFERENCE_RELATIVE_PATH;
        String relativeToSnapshots = filePath.substring(filePath.lastIndexOf(relativePath) + relativePath.length() + 1);
        String[] split = relativeToSnapshots.split(PATH_DELIMITER);
        return split[0] + contextSeparator + split[1].replace("_", contextSeparator); //locale + browser_viewport separated
    }
}
