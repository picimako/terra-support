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

package com.picimako.terra.wdio.imagepreview;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.picimako.terra.wdio.TerraFunctionalTestingScreenshotContextParser;

/**
 * Unit test for {@link TerraFunctionalTestingScreenshotContextParser}.
 */
public class TerraFunctionalTestingScreenshotContextParserTest {

    @Test
    public void shouldReturnScreenshotContextString() {
        String path = "/home/project/tests/wdio/__snapshots__/diff/terra-theme/en/chrome_enormous/some-spec/a_screenshot.png";
        assertThat(new TerraFunctionalTestingScreenshotContextParser().parse(path)).isEqualTo("terra-theme / en / chrome / enormous");
    }

    @Test
    public void shouldReturnScreenshotContextStringFromNestedFolder() {
        String path = "/home/project/tests/wdio/nested/__snapshots__/diff/terra-theme/en/chrome_enormous/some-spec/a_screenshot.png";
        assertThat(new TerraFunctionalTestingScreenshotContextParser().parse(path)).isEqualTo("terra-theme / en / chrome / enormous");
    }
}
