//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Test;

/**
 * Unit test for {@link FileTypePreconditions}.
 */
public class FileTypePreconditionsTest {

    //isInWdioSpecFile

    @Test
    public void shouldBeInWdioSpecFile() {
        PsiElement psiElement = mockPsiElement("some-spec.js");

        assertThat(FileTypePreconditions.isInWdioSpecFile(psiElement)).isTrue();
    }

    @Test
    public void shouldNotBeInWdioSpecFile() {
        PsiElement psiElement = mockPsiElement("some-test.js");

        assertThat(FileTypePreconditions.isInWdioSpecFile(psiElement)).isFalse();
    }

    //isWdioSpecFile

    @Test
    public void shouldBeWdioSpecFile() {
        PsiFile psiElement = mockPsiFile("some-spec.js");

        assertThat(FileTypePreconditions.isWdioSpecFile(psiElement)).isTrue();
    }

    @Test
    public void shouldNotBeWdioSpecFile() {
        PsiFile psiElement = mockPsiFile("some-test.js");

        assertThat(FileTypePreconditions.isWdioSpecFile(psiElement)).isFalse();
    }

    //isInAppJsxFile

    @Test
    public void shouldBeInNonTestJsxFile() {
        PsiElement psiElement = mockPsiElement("some-component.jsx");

        assertThat(FileTypePreconditions.isInAppJsxFile(psiElement)).isTrue();
    }

    @Test
    public void shouldNotBeInNonTestJsxFile() {
        PsiElement psiElement = mockPsiElement("some-component.test.js");

        assertThat(FileTypePreconditions.isInAppJsxFile(psiElement)).isFalse();
    }

    private PsiElement mockPsiElement(String fileName) {
        PsiElement psiElement = mock(PsiElement.class);
        PsiFile containingFile = mock(PsiFile.class);
        when(psiElement.getContainingFile()).thenReturn(containingFile);
        when(containingFile.getName()).thenReturn(fileName);
        return psiElement;
    }
    
    private PsiFile mockPsiFile(String fileName) {
        PsiFile file = mock(PsiFile.class);
        when(file.getName()).thenReturn(fileName);
        return file;
    }
}
