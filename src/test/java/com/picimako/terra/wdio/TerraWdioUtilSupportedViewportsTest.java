//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link TerraWdioPsiUtil}.
 */
@RunWith(Parameterized.class)
public class TerraWdioUtilSupportedViewportsTest {

    @Parameters
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {"tiny"}, {"small"}, {"medium"}, {"large"}, {"huge"}, {"enormous"}
        });
    }

    @Parameter
    public String viewport;

    @Test
    public void shouldSupportViewport() {
        assertThat(TerraWdioPsiUtil.isSupportedViewport(viewport)).isTrue();
    }
}
