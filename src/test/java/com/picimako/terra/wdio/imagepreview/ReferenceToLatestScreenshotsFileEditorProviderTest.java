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
import static org.mockito.Mockito.when;

import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.images.fileTypes.impl.ImageFileType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Unit test for {@link ReferenceToLatestScreenshotsFileEditorProvider}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReferenceToLatestScreenshotsFileEditorProviderTest {

    @Mock
    private Project project;
    @Mock
    private VirtualFile file;

    @Test
    public void shouldAcceptLatestImageFile() {
        validateFileAcceptance(true, true, false, true);
    }

    @Test
    public void shouldAcceptReferenceImageFile() {
        validateFileAcceptance(true, false, true, true);
    }

    @Test
    public void shouldNotAcceptDiffImageFile() {
        validateFileAcceptance(true, false, false, false);
    }

    @Test
    public void shouldNotAcceptLatestNotImageFile() {
        validateFileAcceptance(false, true, false, false);
    }

    @Test
    public void shouldNotAcceptReferenceNotImageFile() {
        validateFileAcceptance(false, false, true, false);
    }

    private void validateFileAcceptance(boolean isFileOfTypeMockResult, boolean isLatestScreenshotMockResult, boolean isReferenceScreenshotMockResult, boolean expectedResult) {
        try (MockedStatic<FileTypeRegistry> registry = Mockito.mockStatic(FileTypeRegistry.class);
             MockedStatic<TerraWdioFolders> folders = Mockito.mockStatic(TerraWdioFolders.class)) {
            FileTypeRegistry fileTypeRegistry = mock(FileTypeRegistry.class);
            registry.when(FileTypeRegistry::getInstance).thenReturn(fileTypeRegistry);
            when(fileTypeRegistry.isFileOfType(file, ImageFileType.INSTANCE)).thenReturn(isFileOfTypeMockResult);
            folders.when(() -> TerraWdioFolders.isLatestScreenshot(file, project)).thenReturn(isLatestScreenshotMockResult);
            folders.when(() -> TerraWdioFolders.isReferenceScreenshot(file, project)).thenReturn(isReferenceScreenshotMockResult);

            assertThat(new ReferenceToLatestScreenshotsFileEditorProvider().accept(project, file)).isEqualTo(expectedResult);
        }
    }
}
