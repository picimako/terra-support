//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Test;

/**
 * Unit test for {@link ScreenshotDiff}.
 */
public class ScreenshotDiffTest {

    @Test
    public void shouldHaveLatest() {
        ScreenshotDiff diff = new ScreenshotDiff(mock(VirtualFile.class), mock(VirtualFile.class));

        assertThat(diff.hasLatest()).isTrue();
    }

    @Test
    public void shouldNotHaveLatest() {
        ScreenshotDiff diff = new ScreenshotDiff(mock(VirtualFile.class));

        assertThat(diff.hasLatest()).isFalse();
    }
}
