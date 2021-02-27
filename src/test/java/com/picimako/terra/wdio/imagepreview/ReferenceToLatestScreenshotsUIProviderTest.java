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
