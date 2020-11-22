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

import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITHOUT_IMPORT;
import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITH_IMPORT;
import static com.picimako.terra.JavaScriptTestFileSupport.createJavaScriptFileFromText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlToken;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ReflectionUtil;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.picimako.terra.psi.fs.FileSystemUtil;
import com.picimako.terra.psi.js.ES6ImportUtil;

/**
 * Unit test for {@link TerraUIComponentDocumentationUrlProvider}.
 */
public class TerraUIComponentDocumentationUrlProviderTest extends BasePlatformTestCase {

    private static final String DOCUMENTATION = "{\n" +
            "  \"components\": [\n" +
            "    {\n" +
            "      \"componentName\": \"ResponsiveElement\",\n" +
            "      \"properties\": [\n" +
            "        {\n" +
            "          \"importPath\": \"terra-responsive-element\",\n" +
            "          \"relativeUrl\": \"/terra-responsive-element/responsive-element/responsive-element\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String DOCUMENTATION_HTML ="<code>C:\\projects\\plugindev\\somefilename.js</code>" +
            "<br>" +
            "<a href=\"https://engineering.cerner.com/terra-ui/components/terra-responsive-element/responsive-element/responsive-element\">Terra documentation: ResponsiveElement</a>";

    private final TerraUIComponentDocumentationUrlProvider provider = new TerraUIComponentDocumentationUrlProvider();

    public void testReturnNoDocumentationForNotXmlTokenElement() {
        assertThat(provider.generateDoc(null, null)).isNull();
    }

    public void testReturnNoDocumentationForNoImportPath() {
        XmlToken originalElement = mockOriginalElement("Grid");
        when(originalElement.getContainingFile()).thenReturn(createJavaScriptFileFromText(getProject(), FILE_WITHOUT_IMPORT));

        assertThat(provider.generateDoc(null, originalElement)).isNull();
    }

    public void testReturnNoDocumentationForNonTerraComponent() {
        XmlToken originalElement = mockOriginalElement("SomeNotTerraComponent");
        when(originalElement.getContainingFile()).thenReturn(createJavaScriptFileFromText(getProject(), FILE_WITH_IMPORT));

        assertThat(provider.generateDoc(null, originalElement)).isNull();
    }

    public void testReturnNoDocumentationForUnknownTerraComponent() {
        XmlToken originalElement = mockOriginalElement("UnknownTerraComponent");
        PsiElement element = mock(PsiElement.class);
        DocumentationComponents components = mock(DocumentationComponents.class);
        when(components.findComponentByName("UnknownTerraComponent")).thenReturn(null);
        setProviderDocumentationComponents(components);

        try (MockedStatic<ES6ImportUtil> importUtil = Mockito.mockStatic(ES6ImportUtil.class);
             MockedStatic<FileSystemUtil> fileSystemUtil = Mockito.mockStatic(FileSystemUtil.class)) {

            importUtil.when(() -> ES6ImportUtil.importPathForBaseComponent(originalElement)).thenReturn("terra-unknown-component");
            fileSystemUtil.when(() -> FileSystemUtil.filePathOf(element)).thenReturn("C:\\projects\\plugindev\\somefilename.js");

            assertThat(provider.generateDoc(element, originalElement)).isNull();
        }
    }

    public void testLoadsAndGeneratesDocumentationWhenServiceIsNotYetLoaded() {
        DocumentationComponents components = new Gson().fromJson(DOCUMENTATION, DocumentationComponents.class);
        XmlToken originalElement = mockOriginalElement("ResponsiveElement");
        PsiElement element = mock(PsiElement.class);
        TerraUIComponentDocumentationUrlService service = mock(TerraUIComponentDocumentationUrlService.class);
        when(service.getDocs()).thenReturn(components);

        String documentationHtml;
        try (MockedStatic<ES6ImportUtil> importUtil = Mockito.mockStatic(ES6ImportUtil.class);
             MockedStatic<FileSystemUtil> fileSystemUtil = Mockito.mockStatic(FileSystemUtil.class);
             MockedStatic<ServiceManager> serviceManager = Mockito.mockStatic(ServiceManager.class)) {

            importUtil.when(() -> ES6ImportUtil.importPathForBaseComponent(originalElement)).thenReturn("terra-responsive-element");
            fileSystemUtil.when(() -> FileSystemUtil.filePathOf(element)).thenReturn("C:\\projects\\plugindev\\somefilename.js");
            serviceManager.when(() -> ServiceManager.getService(TerraUIComponentDocumentationUrlService.class)).thenReturn(service);

            documentationHtml = provider.generateDoc(element, originalElement);

            serviceManager.verify(
                    times(1),
                    () -> ServiceManager.getService(TerraUIComponentDocumentationUrlService.class));
        }

        assertThat(documentationHtml).isEqualTo(DOCUMENTATION_HTML);
    }

    public void testGeneratesDocumentationWhenServiceIsAlreadyLoaded() {
        XmlToken originalElement = mockOriginalElement("ResponsiveElement");
        PsiElement element = mock(PsiElement.class);
        DocumentationComponents components = new Gson().fromJson(DOCUMENTATION, DocumentationComponents.class);
        setProviderDocumentationComponents(components);

        String documentationHtml;
        try (MockedStatic<ES6ImportUtil> importUtil = Mockito.mockStatic(ES6ImportUtil.class);
             MockedStatic<FileSystemUtil> fileSystemUtil = Mockito.mockStatic(FileSystemUtil.class);
             MockedStatic<ServiceManager> serviceManager = Mockito.mockStatic(ServiceManager.class)) {

            importUtil.when(() -> ES6ImportUtil.importPathForBaseComponent(originalElement)).thenReturn("terra-responsive-element");
            fileSystemUtil.when(() -> FileSystemUtil.filePathOf(element)).thenReturn("C:\\projects\\plugindev\\somefilename.js");

            documentationHtml = provider.generateDoc(element, originalElement);

            serviceManager.verify(
                    times(0),
                    () -> ServiceManager.getService(TerraUIComponentDocumentationUrlService.class)
            );
        }
        assertThat(documentationHtml).isEqualTo(DOCUMENTATION_HTML);
    }

    private XmlToken mockOriginalElement(String componentName) {
        XmlToken originalElement = mock(XmlToken.class);
        when(originalElement.getText()).thenReturn(componentName);
        return originalElement;
    }

    private void setProviderDocumentationComponents(DocumentationComponents components) {
        ReflectionUtil.setField(TerraUIComponentDocumentationUrlProvider.class, provider,
                DocumentationComponents.class, "documentation", components);
    }
}
