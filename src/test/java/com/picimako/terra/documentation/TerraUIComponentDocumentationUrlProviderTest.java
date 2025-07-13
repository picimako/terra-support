//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.documentation;

import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITHOUT_IMPORT;
import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITH_IMPORT;
import static com.picimako.terra.JavaScriptTestFileSupport.createJavaScriptFileFromText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlToken;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ReflectionUtil;

import com.picimako.terra.psi.fs.FileSystemUtil;
import com.picimako.terra.psi.js.ES6ImportUtil;

/**
 * Unit test for {@link TerraUIComponentDocumentationUrlProvider}.
 */
public class TerraUIComponentDocumentationUrlProviderTest extends BasePlatformTestCase {

    private static final String DOCUMENTATION = """
        {
          "components": [
            {
              "componentName": "ResponsiveElement",
              "properties": [
                {
                  "importPath": "terra-responsive-element",
                  "relativeUrl": "/terra-responsive-element/responsive-element/responsive-element"
                }
              ]
            }
          ]
        }""";

    private static final String DOCUMENTATION_HTML = "<code>C:\\projects\\plugindev\\somefilename.js</code>" +
        "<br>" +
        "<a href=\"https://engineering.cerner.com/terra-ui/components/terra-responsive-element/responsive-element/responsive-element\">Terra documentation: ResponsiveElement</a>";

    private final TerraUIComponentDocumentationUrlProvider provider = new TerraUIComponentDocumentationUrlProvider();

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }

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

        try (var importUtil = mockStatic(ES6ImportUtil.class); var fileSystemUtil = mockStatic(FileSystemUtil.class)) {

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
        try (var importUtil = mockStatic(ES6ImportUtil.class); var fileSystemUtil = mockStatic(FileSystemUtil.class)) {

            importUtil.when(() -> ES6ImportUtil.importPathForBaseComponent(originalElement)).thenReturn("terra-responsive-element");
            fileSystemUtil.when(() -> FileSystemUtil.filePathOf(element)).thenReturn("C:\\projects\\plugindev\\somefilename.js");

            assertThat(provider.getDocumentation()).isNull();
            documentationHtml = provider.generateDoc(element, originalElement);
        }

        assertThat(provider.getDocumentation()).isNotNull();
        assertThat(documentationHtml).isEqualTo(DOCUMENTATION_HTML);
    }

    public void testGeneratesDocumentationWhenServiceIsAlreadyLoaded() {
        XmlToken originalElement = mockOriginalElement("ResponsiveElement");
        PsiElement element = mock(PsiElement.class);
        DocumentationComponents components = new Gson().fromJson(DOCUMENTATION, DocumentationComponents.class);
        setProviderDocumentationComponents(components);

        String documentationHtml;
        try (var importUtil = mockStatic(ES6ImportUtil.class); var fileSystemUtil = mockStatic(FileSystemUtil.class)) {

            importUtil.when(() -> ES6ImportUtil.importPathForBaseComponent(originalElement)).thenReturn("terra-responsive-element");
            fileSystemUtil.when(() -> FileSystemUtil.filePathOf(element)).thenReturn("C:\\projects\\plugindev\\somefilename.js");

            assertThat(provider.getDocumentation()).isNotNull();
            documentationHtml = provider.generateDoc(element, originalElement);
        }

        assertThat(provider.getDocumentation()).isNotNull();
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
