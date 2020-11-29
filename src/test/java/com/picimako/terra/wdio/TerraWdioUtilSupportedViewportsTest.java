/*
 * Copyright 2020 Tamás Balog
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
