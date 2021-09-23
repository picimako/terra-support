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

import static com.picimako.terra.psi.fs.FileSystemUtil.filePathOf;
import static com.picimako.terra.psi.js.ES6ImportUtil.importPathForBaseComponent;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import com.picimako.terra.documentation.DocumentationComponents.Component;
import com.picimako.terra.documentation.DocumentationComponents.ComponentProperties;

/**
 * Provides Terra UI Component documentation URLs for Quick Documentation when it is triggered on Terra Component tags in React JS files.
 * <p>
 * Documentation URLs are handled by an
 * <a href="https://jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_services.html#light-services">application service</a>
 * so that data is initialized only when first queried, after that each further query will return the same object instance.
 * <p>
 * Thus, the documentation URLs are loaded only when the Quick Documentation popup is first triggered, and each further trigger of it
 * will retrieve the data from the already loaded, underlying service-provided object.
 * <p>
 * <b>Interference with other DocumentationProviders</b>
 * <p>
 * This documentation provider might interfere with other {@link DocumentationProvider}s when the Terra component is imported
 * but is not, or not yet, installed via npm. In that case the Quick Documentation popup may show different content or none at all.
 * <p>
 * <b>Plugin debug information</b>
 * <p>
 * Having this documentation provider added, the on-hover quick documentation doesn't appear when the test IDE environment is started via IntelliJ,
 * but works fine when the plugin is actually installed in the IDE.
 */
public class TerraUIComponentDocumentationUrlProvider implements DocumentationProvider {

    private static final String TERRA_COMPONENT_LINK_FORMAT = "<a href=\"https://engineering.cerner.com/terra-ui/components%s\">Terra documentation: %s%s</a>";
    private DocumentationComponents documentation;

    /**
     * {@inheritDoc}
     * <p>
     * Using either DEFINITION or CONTENT section breaks the bottom info section of the Quick Documentation popup
     * that is added regardless of this documentation provider.
     * <p>
     * File type validation doesn't need to happen, it is configured accordingly in the {@code plugin.xml}.
     */
    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (originalElement instanceof XmlToken) {
            final String importPath = importPathForBaseComponent(originalElement);
            if (importPath != null && importPath.startsWith("terra")) {
                if (documentation == null) {
                    documentation = loadDocumentation();
                }
                return generateDocUrl(element, originalElement, importPath);
            }
        }
        return null;
    }

    private DocumentationComponents loadDocumentation() {
        return ApplicationManager.getApplication().getService(TerraUIComponentDocumentationUrlService.class).getDocs();
    }

    /**
     * Generates the content of the Quick Documentation popup including the files path in a system-dependent format,
     * and the additional Terra external documentation URL for the requested component.
     * <p>
     * The file path is also built and included here explicitly because the {@link DocumentationProvider} that provided that path
     * couldn't be found to have this class extend it, and just call its super methods.
     * <p>
     * At this point originalElement will be a non-null value.
     *
     * @param element         the element to get the file path of (the actual implementation JS file the component references)
     * @param originalElement the XML tag element that identifies the component
     * @return the content of the Quick Documentation popup, or null if there is no such Terra component
     */
    @Nullable
    private String generateDocUrl(PsiElement element, @NotNull PsiElement originalElement, @NotNull String importPath) {
        final Component component = documentation.findComponentByName(originalElement.getText());
        if (component != null) {
            return "<code>" + filePathOf(element) + "</code><br>" + getDocumentationLinkFor(component, importPath);
        }
        //This can happen when the component is a Terra one, but not yet added to the documentation resource file: terra-ui-component-docs.json
        return null;
    }

    /**
     * Generates an HTML &lt;a> tag having its href configured to the external documentation URL of the argument component,
     * and the link text configured as detailed below.
     * <p>
     * If the component is part of a single family, the family name is not included. For the following input values:
     * <pre>
     * component.componentName: ResponsiveElement
     * importPath:             terra-responsive-element
     * </pre>
     * the returned link text will be "{@code Terra documentation: ResponsiveElement}".
     * <p>
     * If the component is part of multiple families, it generates the external URL by from where the component
     * is imported from, and includes the family name as well. For the following input values:
     * <pre>
     * component.componentName: IconEdit
     * importPath:             terra-icon/lib/icon/IconEdit
     * </pre>
     * the return link text will be "{@code Terra documentation: Icon / IconEdit}".
     *
     * @param component  the component to get the documentation URL of
     * @param importPath the import path where the component is imported from
     * @return the HTML code for the external documentation link
     */
    @NotNull
    private String getDocumentationLinkFor(Component component, @NotNull String importPath) {
        final ComponentProperties properties = component.findPropertiesByImportPath(trimApostrophes(importPath));
        return String.format(TERRA_COMPONENT_LINK_FORMAT,
                properties.relativeUrl,
                properties.family.isBlank() ? "" : properties.family + " / ",
                component.componentName);
    }

    /**
     * Trims the starting and ending apostrophes from the argument string.
     */
    private String trimApostrophes(String importPath) {
        return importPath.replace("'", "");
    }

    @TestOnly
    DocumentationComponents getDocumentation() {
        return documentation;
    }
}
