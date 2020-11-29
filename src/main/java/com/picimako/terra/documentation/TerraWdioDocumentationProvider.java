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

import static com.intellij.lang.documentation.DocumentationMarkup.CONTENT_END;
import static com.intellij.lang.documentation.DocumentationMarkup.CONTENT_START;
import static com.picimako.terra.FileTypePreconditionsUtil.isInWdioSpecFile;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isAnyOfTerraWdioFunctions;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Properties;

import com.intellij.lang.javascript.documentation.JavaScriptDocumentationProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides external documentation links for Terra wdio objects and functions in Terra wdio spec files.
 * <p>
 * The links are added in addition to the original documentation content what their Quick Documentation popup provides,
 * which could be achieved by having this class extended from {@link JavaScriptDocumentationProvider}.
 * <p>
 * Documentation URLs are handled by an
 * <a href="https://jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_services.html#light-services">application service</a>
 * because application services are initialized only when they are first queried from the {@link ServiceManager}, after that each further
 * query will return the same object.
 * <p>
 * Thus, the documentation URLs are loaded only when the Quick Documentation popup is first triggered, and each further trigger of it
 * will retrieve the data from the already loaded, underlying service-provided object.
 */
public class TerraWdioDocumentationProvider extends JavaScriptDocumentationProvider {

    private static final String HTML_LINK = "<a href=\"%s\">%s</a>";
    private Properties documentation;

    /**
     * {@inheritDoc}
     * <p>
     * Generates the documentation only when the element on which the Quick Documentation is invoked is a Terra function or object,
     * and it is within a Terra wdio spec file.
     */
    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (originalElement != null && isInWdioSpecFile(originalElement) && isAnyOfTerraWdioFunctions(originalElement)) {
            if (documentation == null) {
                documentation = loadDocumentation();
            }
            return super.generateDoc(element, originalElement) + generateDocLinks(originalElement);
        }
        return null;
    }

    /**
     * Generates the documentation HTML snippet for the popup.
     * <p>
     * Each entry in the source properties file may have one or more links separated by commas, and the text and URL
     * parts of each links separated by pipe (|) characters.
     * <p>
     * One example may be (using custom placeholders for common values):
     * <pre>
     * Terra.it.isAccessible={terra} / Test Assertion Helpers|{wdioutil}#test-assertion-helpers,Axe Accessibility Rule Descriptions|https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md
     * </pre>
     * Extra trimming and blank/empty string checking is not included here, since the source content is not dynamic.
     * <p>
     * When adding entries into {@code terra-wdio-docs.properties}, please make sure that they are added as non-blank,
     * trimmed values.
     *
     * @param originalElement the element to show the documentation popup for
     * @return the generated HTML code to render in the documentation popup
     */
    @NotNull
    private String generateDocLinks(PsiElement originalElement) {
        String terraElementText = originalElement.getParent().getText();
        String[] links = documentation.getProperty(terraElementText).split(",");
        return CONTENT_START + Arrays.stream(links)
                .map(link -> {
                    String[] linkProps = link.split("\\|");
                    return String.format(HTML_LINK, resolvePlaceholders(linkProps[1]), resolvePlaceholders(linkProps[0]));
                }).collect(joining("<br>")) + CONTENT_END;
    }

    private Properties loadDocumentation() {
        return ServiceManager.getService(TerraWdioDocumentationService.class).getDocs();
    }

    /**
     * To shorten and simplify the content of the source properties file, it incorporates some custom placeholders for
     * common values which are resolved by this method.
     *
     * @param link the link to resolve the placeholders in
     * @return the link having the placeholders replaced in it
     */
    private String resolvePlaceholders(String link) {
        return link.replace("{terra}", "Webdriver.io Utility Developer's Guide")
                .replace("{wdioutil}", "https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md");
    }
}
