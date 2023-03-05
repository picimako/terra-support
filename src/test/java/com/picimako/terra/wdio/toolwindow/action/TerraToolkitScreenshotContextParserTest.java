//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.picimako.terra.wdio.TerraToolkitScreenshotContextParser;

/**
 * Unit test for {@link TerraToolkitScreenshotContextParser}.
 */
public class TerraToolkitScreenshotContextParserTest {

    @Test
    public void shouldReturnScreenshotContextString() {
        String path = "/home/project/tests/wdio/__snapshots__/diff/en/chrome_enormous/some-spec/a_screenshot.png";
        assertThat(TerraToolkitScreenshotContextParser.INSTANCE.parse(path)).isEqualTo("en / chrome / enormous");
    }

    @Test
    public void shouldReturnScreenshotContextStringFromNestedFolder() {
        String path = "/home/project/tests/wdio/nested/__snapshots__/diff/en/chrome_enormous/some-spec/a_screenshot.png";
        assertThat(TerraToolkitScreenshotContextParser.INSTANCE.parse(path)).isEqualTo("en / chrome / enormous");
    }
}
