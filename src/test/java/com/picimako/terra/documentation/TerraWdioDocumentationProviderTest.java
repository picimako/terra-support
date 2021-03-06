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

package com.picimako.terra.documentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Properties;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.picimako.terra.FileTypePreconditions;
import com.picimako.terra.wdio.TerraWdioPsiUtil;

/**
 * Unit test for {@link TerraWdioDocumentationProvider}.
 */
public class TerraWdioDocumentationProviderTest extends BasePlatformTestCase {

    private final TerraWdioDocumentationProvider provider = new TerraWdioDocumentationProvider();

    public void testReturnNoDocumentationWhenNoOriginalElement() {
        assertThat(provider.generateDoc(null, null)).isNull();
    }

    public void testReturnNoDocumentationWhenContainingFileIsNotASpecFile() {
        PsiElement originalElement = mock(PsiElement.class);
        try (MockedStatic<FileTypePreconditions> fileTypeUtil = Mockito.mockStatic(FileTypePreconditions.class)) {
            fileTypeUtil.when(() -> FileTypePreconditions.isInWdioSpecFile(originalElement)).thenReturn(false);

            assertThat(provider.generateDoc(null, originalElement)).isNull();
        }
    }

    public void testReturnNoDocumentationWhenOriginalElementIsNotTerraWdio() {
        PsiElement originalElement = mock(PsiElement.class);
        try (MockedStatic<TerraWdioPsiUtil> importUtil = Mockito.mockStatic(TerraWdioPsiUtil.class);
             MockedStatic<FileTypePreconditions> fileTypeUtil = Mockito.mockStatic(FileTypePreconditions.class)) {
            fileTypeUtil.when(() -> FileTypePreconditions.isInWdioSpecFile(originalElement)).thenReturn(true);
            importUtil.when(() -> TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(originalElement)).thenReturn(false);

            assertThat(provider.generateDoc(null, originalElement)).isNull();
        }
    }

    public void testLoadsAndGeneratesDocumentationWhenServiceIsNotYetLoaded() {
        TerraWdioDocumentationService service = mock(TerraWdioDocumentationService.class);
        PsiElement element = mock(PsiElement.class);
        PsiElement originalElement = mockOriginalElement();
        when(service.getDocs()).thenReturn(setupProperties());

        try (MockedStatic<TerraWdioPsiUtil> terraUtil = Mockito.mockStatic(TerraWdioPsiUtil.class);
             MockedStatic<FileTypePreconditions> fileTypeUtil = Mockito.mockStatic(FileTypePreconditions.class);
             MockedStatic<ServiceManager> serviceManager = Mockito.mockStatic(ServiceManager.class)) {
            fileTypeUtil.when(() -> FileTypePreconditions.isInWdioSpecFile(originalElement)).thenReturn(true);
            terraUtil.when(() -> TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(originalElement)).thenReturn(true);
            serviceManager.when(() -> ServiceManager.getService(TerraWdioDocumentationService.class)).thenReturn(service);

            assertThat(provider.generateDoc(element, originalElement)).isEqualTo("null<div class='content'><a href=\"https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md#test-assertion-helpers\">Webdriver.io Utility Developer's Guide / Test Assertion Helpers</a><br><a href=\"https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md\">Axe Accessibility Rule Descriptions</a></div>");

            serviceManager.verify(
                times(1),
                () -> ServiceManager.getService(TerraWdioDocumentationService.class));
        }
    }

    public void testLoadsAndGeneratesDocumentationWhenServiceIsAlreadyLoaded() {
        PsiElement element = mock(PsiElement.class);
        PsiElement originalElement = mockOriginalElement();
        setProviderDocumentation(setupProperties());

        try (MockedStatic<TerraWdioPsiUtil> terraUtil = Mockito.mockStatic(TerraWdioPsiUtil.class);
             MockedStatic<FileTypePreconditions> fileTypeUtil = Mockito.mockStatic(FileTypePreconditions.class);
             MockedStatic<ServiceManager> serviceManager = Mockito.mockStatic(ServiceManager.class)) {
            fileTypeUtil.when(() -> FileTypePreconditions.isInWdioSpecFile(originalElement)).thenReturn(true);
            terraUtil.when(() -> TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(originalElement)).thenReturn(true);

            assertThat(provider.generateDoc(element, originalElement)).isEqualTo("null<div class='content'><a href=\"https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md#test-assertion-helpers\">Webdriver.io Utility Developer's Guide / Test Assertion Helpers</a><br><a href=\"https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md\">Axe Accessibility Rule Descriptions</a></div>");

            serviceManager.verify(
                times(0),
                () -> ServiceManager.getService(TerraWdioDocumentationService.class));
        }
    }

    @NotNull
    private PsiElement mockOriginalElement() {
        PsiElement originalElement = mock(PsiElement.class);
        PsiElement parent = mock(PsiElement.class);
        when(originalElement.getParent()).thenReturn(parent);
        when(parent.getText()).thenReturn("Terra.it.isAccessible");
        return originalElement;
    }

    @NotNull
    private Properties setupProperties() {
        Properties properties = new Properties();
        properties.put("Terra.it.isAccessible", "{terra} / Test Assertion Helpers|{wdioutil}#test-assertion-helpers,Axe Accessibility Rule Descriptions|https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md");
        return properties;
    }

    private void setProviderDocumentation(Properties properties) {
        ReflectionUtil.setField(TerraWdioDocumentationProvider.class, provider, Properties.class, "documentation", properties);
    }
}
