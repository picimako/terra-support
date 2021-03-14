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

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link TerraWdioTreeNode}.
 */
public class TerraWdioTreeNodeTest {

    @Test
    public void shouldBeSpec() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", null);

        assertThat(TerraWdioTreeNode.isSpec(spec)).isTrue();
    }

    @Test
    public void shouldNotBeSpec() {
        TerraWdioTreeModelDataRoot root = new TerraWdioTreeModelDataRoot("root", null);

        assertThat(TerraWdioTreeNode.isSpec(root)).isFalse();
    }

    @Test
    public void shouldBeScreenshot() {
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", null);

        assertThat(TerraWdioTreeNode.isScreenshot(screenshot)).isTrue();
    }

    @Test
    public void shouldNotBeScreenshot() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", null);

        assertThat(TerraWdioTreeNode.isScreenshot(spec)).isFalse();
    }
}
