//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
        assertThat(TerraFunctionalTestingScreenshotContextParser.INSTANCE.parse(path)).isEqualTo("terra-theme / en / chrome / enormous");
    }

    @Test
    public void shouldReturnScreenshotContextStringFromNestedFolder() {
        String path = "/home/project/tests/wdio/nested/__snapshots__/diff/terra-theme/en/chrome_enormous/some-spec/a_screenshot.png";
        assertThat(TerraFunctionalTestingScreenshotContextParser.INSTANCE.parse(path)).isEqualTo("terra-theme / en / chrome / enormous");
    }
}
