//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Test;

/**
 * Unit test for {@link ReferenceToLatestScreenshotsUIProvider}.
 */
public class ReferenceToLatestScreenshotsUIProviderTest {

    @Test
    public void shouldReturnNullWhenDiffHasNoLatestImage() {
        Project project = mock(Project.class);
        VirtualFile original = mock(VirtualFile.class);

        ReferenceToLatestScreenshotsUIProvider provider = new ReferenceToLatestScreenshotsUIProvider(project);
        assertThat(provider.getContent(new ScreenshotDiff(original))).isNull();
    }
}
