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
