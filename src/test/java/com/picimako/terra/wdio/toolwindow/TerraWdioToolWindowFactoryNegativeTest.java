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

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraToolkitManager;

/**
 * Unit test for {@link TerraWdioToolWindowFactory}.
 */
public class TerraWdioToolWindowFactoryNegativeTest extends BasePlatformTestCase {

    public void testNotAvailableForNonExistentWdioRoot() {
        TerraResourceManager.getInstance(getProject(), TerraToolkitManager.class);

        assertThat(new TerraWdioToolWindowFactory().shouldBeAvailable(getProject())).isFalse();
    }
}
