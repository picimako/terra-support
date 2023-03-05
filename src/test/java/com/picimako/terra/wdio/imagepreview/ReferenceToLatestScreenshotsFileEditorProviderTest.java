//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
