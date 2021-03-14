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
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraWdioTreeModelDataRoot}.
 */
public class TerraWdioTreeModelDataRootTest extends BasePlatformTestCase {

    private TerraWdioTreeModelDataRoot root;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root = new TerraWdioTreeModelDataRoot("Screenshots", getProject());
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = true;
    }

    public void testReturnToStringWithSingularSpecSingularScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);

        assertThat(root).hasToString("Screenshots (1 spec, 1 screenshot)");
    }

    public void testReturnToStringWithSingularSpecPluralScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);

        assertThat(root).hasToString("Screenshots (1 spec, 2 screenshots)");
    }

    public void testReturnToStringWithPluralSpecSingularScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());
        TerraWdioTreeSpecNode spec2 = new TerraWdioTreeSpecNode("spec2", getProject());
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);
        root.getSpecs().add(spec2);

        assertThat(root).hasToString("Screenshots (2 specs, 1 screenshot)");
    }

    public void testReturnToStringWithPluralSpecPluralScreenshotStat() {
        TerraWdioTreeSpecNode spec = new TerraWdioTreeSpecNode("spec", getProject());
        TerraWdioTreeSpecNode spec2 = new TerraWdioTreeSpecNode("spec2", getProject());
        TerraWdioTreeScreenshotNode screenshot = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        TerraWdioTreeScreenshotNode screenshot2 = new TerraWdioTreeScreenshotNode("screenshot", getProject());
        screenshot2.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        spec.addScreenshot(screenshot2);
        root.getSpecs().add(spec);
        root.getSpecs().add(spec2);

        assertThat(root).hasToString("Screenshots (2 specs, 2 screenshots)");
    }
}
