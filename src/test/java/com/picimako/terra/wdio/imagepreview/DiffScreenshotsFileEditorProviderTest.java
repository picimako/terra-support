//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
 * Unit test for {@link DiffScreenshotsFileEditorProvider}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiffScreenshotsFileEditorProviderTest {

    @Mock
    private Project project;
    @Mock
    private VirtualFile file;

    @Test
    public void shouldAcceptDiffImageFile() {
        validateFileAcceptance(true, true, true);
    }

    @Test
    public void shouldNotAcceptDiffNotImageFile() {
        validateFileAcceptance(false, true, false);
    }

    @Test
    public void shouldNotAcceptNotDiffImageFile() {
        validateFileAcceptance(true, false, false);
    }

    @Test
    public void shouldNotAcceptNotDiffNotImageFile() {
        validateFileAcceptance(false, false, false);
    }

    private void validateFileAcceptance(boolean isFileOfTypeMockResult, boolean isDiffScreenshotMockResult, boolean expectedResult) {
        try (MockedStatic<FileTypeRegistry> registry = Mockito.mockStatic(FileTypeRegistry.class);
             MockedStatic<TerraWdioFolders> folders = Mockito.mockStatic(TerraWdioFolders.class)) {
            FileTypeRegistry fileTypeRegistry = mock(FileTypeRegistry.class);
            registry.when(FileTypeRegistry::getInstance).thenReturn(fileTypeRegistry);
            when(fileTypeRegistry.isFileOfType(file, ImageFileType.INSTANCE)).thenReturn(isFileOfTypeMockResult);
            folders.when(() -> TerraWdioFolders.isDiffScreenshot(file, project)).thenReturn(isDiffScreenshotMockResult);

            assertThat(new DiffScreenshotsFileEditorProvider().accept(project, file)).isEqualTo(expectedResult);
        }
    }
}
