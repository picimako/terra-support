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

import java.io.IOException;
import java.util.Properties;

import com.intellij.openapi.components.Service;

/**
 * Application Service to load and provide documentation links for Terra wdio functions and objects.
 *
 * @see com.picimako.terra.documentation.TerraWdioDocumentationProvider
 */
@Service
public final class TerraWdioDocumentationService {

    private Properties docs;

    public TerraWdioDocumentationService() {
        try {
            docs = new Properties();
            docs.load(getClass().getResourceAsStream("terra-wdio-docs.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            docs = null;
        }
    }

    Properties getDocs() {
        return docs;
    }
}
