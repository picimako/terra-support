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
}
