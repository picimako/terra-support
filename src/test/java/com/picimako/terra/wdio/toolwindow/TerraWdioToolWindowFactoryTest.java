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

package com.picimako.terra.wdio.toolwindow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Unit test for {@link TerraWdioToolWindowFactory}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TerraWdioToolWindowFactoryTest {

    @Mock
    private Project project;

    @Test
    public void shouldNotBeAvailableForDefaultProject() {
        when(project.isDefault()).thenReturn(true);

        TerraWdioToolWindowFactory factory = new TerraWdioToolWindowFactory();

        assertThat(factory.shouldBeAvailable(project)).isFalse();
    }

    @Test
    public void shouldNotBeAvailableForNullWdioRoot() {
        when(project.isDefault()).thenReturn(false);
        try (MockedStatic<TerraWdioFolders> wdioFolders = Mockito.mockStatic(TerraWdioFolders.class)) {
            wdioFolders.when(() -> TerraWdioFolders.projectWdioRoot(project)).thenReturn(null);

            TerraWdioToolWindowFactory factory = new TerraWdioToolWindowFactory();

            assertThat(factory.shouldBeAvailable(project)).isFalse();
        }
    }

    @Test
    public void shouldNotBeAvailableForNonExistentWdioRoot() {
        when(project.isDefault()).thenReturn(false);
        try (MockedStatic<TerraWdioFolders> wdioFolders = Mockito.mockStatic(TerraWdioFolders.class)) {
            VirtualFile wdioRoot = mock(VirtualFile.class);
            when(wdioRoot.exists()).thenReturn(false);
            wdioFolders.when(() -> TerraWdioFolders.projectWdioRoot(project)).thenReturn(wdioRoot);

            TerraWdioToolWindowFactory factory = new TerraWdioToolWindowFactory();

            assertThat(factory.shouldBeAvailable(project)).isFalse();
        }
    }

    @Test
    public void shouldBeAvailableAndShouldConfigureWdioTestRootPath() {
        when(project.isDefault()).thenReturn(false);
        when(project.getBasePath()).thenReturn("/home/project");
        try (MockedStatic<TerraWdioFolders> wdioFolders = Mockito.mockStatic(TerraWdioFolders.class)) {
            VirtualFile wdioRoot = mock(VirtualFile.class);
            when(wdioRoot.exists()).thenReturn(true);
            when(wdioRoot.getPath()).thenReturn("/home/project/tests/wdio");
            wdioFolders.when(() -> TerraWdioFolders.projectWdioRoot(project)).thenReturn(wdioRoot);

            TerraWdioToolWindowFactory factory = new TerraWdioToolWindowFactory();
            assertThat(factory.shouldBeAvailable(project)).isTrue();

            wdioFolders.verify(times(1), () -> TerraWdioFolders.setWdioTestRootPath("/tests/wdio"));
        }
    }
}
