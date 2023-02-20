//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Unit test for {@link TreeModelDataRoot}.
 */
public class TreeModelDataRootTest extends BasePlatformTestCase {

    private TreeModelDataRoot root;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root = new TreeModelDataRoot("Screenshots", getProject());
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = true;
    }

    public void testReturnDisplayNameAsToStringForStatisticsTurnedOff() {
        ScreenshotStatisticsProjectService.getInstance(getProject()).isShowStatistics = false;

        assertThat(root).hasToString("Screenshots");
    }

    public void testReturnToStringWithSingularSpecSingularScreenshotStat() {
        TreeSpecNode spec = new TreeSpecNode("spec", getProject());
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);

        assertThat(root).hasToString("Wdio Resources (1 spec, 1 screenshot)");
    }

    public void testReturnToStringWithSingularSpecPluralScreenshotStat() {
        TreeSpecNode spec = new TreeSpecNode("spec", getProject());
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);

        assertThat(root).hasToString("Wdio Resources (1 spec, 2 screenshots)");
    }

    public void testReturnToStringWithPluralSpecSingularScreenshotStat() {
        TreeSpecNode spec = new TreeSpecNode("spec", getProject());
        TreeSpecNode spec2 = new TreeSpecNode("spec2", getProject());
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        root.getSpecs().add(spec);
        root.getSpecs().add(spec2);

        assertThat(root).hasToString("Wdio Resources (2 specs, 1 screenshot)");
    }

    public void testReturnToStringWithPluralSpecPluralScreenshotStat() {
        TreeSpecNode spec = new TreeSpecNode("spec", getProject());
        TreeSpecNode spec2 = new TreeSpecNode("spec2", getProject());
        TreeScreenshotNode screenshot = new TreeScreenshotNode("screenshot", getProject());
        screenshot.addReference(mock(VirtualFile.class));
        TreeScreenshotNode screenshot2 = new TreeScreenshotNode("screenshot", getProject());
        screenshot2.addReference(mock(VirtualFile.class));
        spec.addScreenshot(screenshot);
        spec.addScreenshot(screenshot2);
        root.getSpecs().add(spec);
        root.getSpecs().add(spec2);

        assertThat(root).hasToString("Wdio Resources (2 specs, 2 screenshots)");
    }
}
