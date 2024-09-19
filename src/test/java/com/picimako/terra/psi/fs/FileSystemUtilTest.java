//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.psi.fs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link FileSystemUtil}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileSystemUtilTest {

    @Mock
    private PsiElement element;
    @Mock
    private PsiFile containingFile;
    @Mock
    private FileViewProvider fileViewProvider;
    @Mock
    private VirtualFile virtualFile;

    @Before
    public void setup() {
        when(element.getContainingFile()).thenReturn(containingFile);
        when(containingFile.getViewProvider()).thenReturn(fileViewProvider);
        when(fileViewProvider.getVirtualFile()).thenReturn(virtualFile);
    }

    @Test
    public void shouldReturnSystemDependentPathForWindowsPath() {
        String path = "C:\\projects\\plugindev\\somefilename.js";
        when(virtualFile.getCanonicalPath()).thenReturn(path);

        assertThat(FileSystemUtil.filePathOf(element)).isEqualTo(path.replace("\\", File.separator));
    }

    @Test
    public void shouldReturnSystemDependentPathForUnixPath() {
        String path = "/projects/plugindev/somefilename.js";
        when(virtualFile.getCanonicalPath()).thenReturn(path);

        assertThat(FileSystemUtil.filePathOf(element)).isEqualTo(path.replace("/", File.separator));
    }

    @Test
    public void shouldReturnFileNameIfContainingFileHasNoPath() {
        when(virtualFile.getCanonicalPath()).thenReturn(null);
        when(containingFile.getName()).thenReturn("somefilename.js");

        assertThat(FileSystemUtil.filePathOf(element)).isEqualTo("somefilename.js");
    }
}
