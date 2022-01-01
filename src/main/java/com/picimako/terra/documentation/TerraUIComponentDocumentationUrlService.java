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

import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.intellij.openapi.components.Service;

/**
 * Application Service to load and provide Terra UI Component names, documentation URLs and other properties.
 *
 * @see TerraUIComponentDocumentationUrlProvider
 */
@Service //Service.Level.APP
public final class TerraUIComponentDocumentationUrlService {

    private final DocumentationComponents componentDocs;

    public TerraUIComponentDocumentationUrlService() {
        componentDocs = new Gson().fromJson(new InputStreamReader(getClass().getResourceAsStream("terra-ui-component-docs.json")), DocumentationComponents.class);
    }

    DocumentationComponents getDocs() {
        return componentDocs;
    }
}
