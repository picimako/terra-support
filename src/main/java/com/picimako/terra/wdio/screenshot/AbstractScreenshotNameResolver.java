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
