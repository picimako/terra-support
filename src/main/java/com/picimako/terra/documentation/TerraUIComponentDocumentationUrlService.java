//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.documentation;

import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.intellij.openapi.components.Service;

/**
 * Application Service to load and provide Terra UI Component names, documentation URLs and other properties.
 *
 * @see TerraUIComponentDocumentationUrlProvider
 */
@Service(Service.Level.APP)
public final class TerraUIComponentDocumentationUrlService {

    private final DocumentationComponents componentDocs;

    public TerraUIComponentDocumentationUrlService() {
        componentDocs = new Gson().fromJson(
            new InputStreamReader(getClass().getResourceAsStream("terra-ui-component-docs.json")),
            DocumentationComponents.class);
    }

    DocumentationComponents getDocs() {
        return componentDocs;
    }
}
