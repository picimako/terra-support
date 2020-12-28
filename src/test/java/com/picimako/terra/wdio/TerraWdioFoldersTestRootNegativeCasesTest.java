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

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link TerraWdioFolders}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TerraWdioFoldersTestRootNegativeCasesTest {

    @Mock
    private Project project;

    // getTestRoot

    @Test
    public void shouldReturnNullRootWhenProjectDirectoryNeverExisted() {
        try (MockedStatic<ProjectUtil> projectUtil = Mockito.mockStatic(ProjectUtil.class)) {
            projectUtil.when(() -> ProjectUtil.guessProjectDir(project)).thenReturn(null);

            assertThat(TerraWdioFolders.getTestRoot(project, "wdio")).isNull();
        }
    }

    @Test
    public void shouldReturnNullRootWhenProjectDirectoryDoesNotExist() {
        VirtualFile projectDirectory = mock(VirtualFile.class);
        try (MockedStatic<ProjectUtil> projectUtil = Mockito.mockStatic(ProjectUtil.class)) {
            projectUtil.when(() -> ProjectUtil.guessProjectDir(project)).thenReturn(projectDirectory);

            assertThat(TerraWdioFolders.getTestRoot(project, "wdio")).isNull();
        }
    }

    @Test
    public void shouldReturnNullWhenNoTestTypeSpecificTestRootExists() {
        VirtualFile projectDirectory = mock(VirtualFile.class);
        VirtualFile testsRoot = mock(VirtualFile.class);
        try (MockedStatic<ProjectUtil> projectUtil = Mockito.mockStatic(ProjectUtil.class)) {
            projectUtil.when(() -> ProjectUtil.guessProjectDir(project)).thenReturn(projectDirectory);
            when(projectDirectory.exists()).thenReturn(true);
            when(projectDirectory.findChild("tests")).thenReturn(testsRoot);

            assertThat(TerraWdioFolders.getTestRoot(project, "wdio")).isNull();
        }
    }
}
