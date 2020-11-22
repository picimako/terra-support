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
import static org.mockito.Mockito.mock;

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link TerraWdioTreeModelDataRoot}.
 */
public class TerraWdioTreeModelDataRootTest {

    private TerraWdioTreeModelDataRoot root;

    @Before
    public void setup() {
        root = new TerraWdioTreeModelDataRoot("Screenshots");
    }

    @Test
    public void shouldReturnToStringWithSingularSpecSingularScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec");
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);

        assertThat(root).hasToString("Screenshots (1 spec, 1 screenshot)");
    }

    @Test
    public void shouldReturnToStringWithSingularSpecPluralScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec");
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot.addReference(mock(VirtualFile.class));
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);

        assertThat(root).hasToString("Screenshots (1 spec, 2 screenshots)");
    }

    @Test
    public void shouldReturnToStringWithPluralSpecSingularScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec");
        TerraWdioTreeSpecNode spec2 = new TerraWdioTreeSpecNode("spec2");
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);
        root.getSpecs().add(spec2);

        assertThat(root).hasToString("Screenshots (2 specs, 1 screenshot)");
    }

    @Test
    public void shouldReturnToStringWithPluralSpecPluralScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec");
        TerraWdioTreeSpecNode spec2 = new TerraWdioTreeSpecNode("spec2");
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot.addReference(mock(VirtualFile.class));
        TerraWdioTreeScreenshotNode screenshot2 = new TerraWdioTreeScreenshotNode("screenshot");
        screenshot2.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        spec.addScreenshot(screenshot2);
        root.getSpecs().add(spec);
        root.getSpecs().add(spec2);

        assertThat(root).hasToString("Screenshots (2 specs, 2 screenshots)");
    }
}
