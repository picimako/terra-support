//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common class for screenshot name resolution.
 * <p>
 * Provides the logic for normalizing the screenshot names and parsing the test ids from the name arguments.
 */
public abstract class AbstractScreenshotNameResolver implements ScreenshotNameResolver {

    /**
     * The regular expression for the character replacement can be found in
     * <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/config/wdio/visualRegressionConf.js">terra-toolkit-boneyard//visualRegressionConf.js</a>
     * and
     * <a href="https://github.com/cerner/terra-toolkit/blob/main/packages/terra-functional-testing/src/services/wdio-visual-regression-service/methods/BaseCompare.js</a>
     */
    protected static final String DELIMITERS_TO_REPLACE = "\\s+|\\.|\\+";
    protected static final String CHARACTERS_TO_REPLACE = "[?<>/|*:+\"]";

    protected static final Pattern TEST_ID_PATTERN = Pattern.compile("\\[(?<testId>[^)]+)]");

    /**
     * Removes and replaces necessary characters in the argument text, so that the screenshot file name can be built properly.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    protected String normalize(String text) {
        return text.replaceAll(DELIMITERS_TO_REPLACE, "_").replaceAll(CHARACTERS_TO_REPLACE, "-");
    }

    protected String parseTestId(String partialName) {
        Matcher testIdMatcher = TEST_ID_PATTERN.matcher(partialName);
        return testIdMatcher.find() ? testIdMatcher.group("testId") : partialName;
    }
}
