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

import com.google.gson.Gson;
import org.junit.Test;

import com.picimako.terra.documentation.DocumentationComponents.Component;
import com.picimako.terra.documentation.DocumentationComponents.ComponentProperties;

/**
 * Unit test for {@link com.picimako.terra.documentation.DocumentationComponents}.
 */
public class DocumentationComponentsTest {

    private static final String DOCUMENTATION = "{\n" +
        "  \"components\": [\n" +
        "    {\n" +
        "      \"componentName\": \"ActionFooter\",\n" +
        "      \"properties\": [\n" +
        "        {\n" +
        "          \"importPath\": \"terra-action-footer\",\n" +
        "          \"relativeUrl\": \"/terra-action-footer/action-footer/standard\"\n" +
        "        }\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"componentName\": \"ActionHeader\",\n" +
        "      \"properties\": [\n" +
        "        {\n" +
        "          \"importPath\": \"terra-action-header\",\n" +
        "          \"relativeUrl\": \"/terra-action-header/action-header/action-header\"\n" +
        "        }\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"componentName\": \"Item\",\n" +
        "      \"properties\": [\n" +
        "        {\n" +
        "          \"family\": \"Dropdown Button\",\n" +
        "          \"importPath\": \"terra-dropdown-button\",\n" +
        "          \"relativeUrl\": \"/terra-dropdown-button/dropdown-button/dropdown-button\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"family\": \"List\",\n" +
        "          \"importPath\": \"terra-list/lib/index\",\n" +
        "          \"relativeUrl\": \"/terra-list/list/list-item\"\n" +
        "        }\n" +
        "      ]\n" +
        "    }\n" +
        "  ]\n" +
        "}";
    private static final DocumentationComponents COMPONENTS = new Gson().fromJson(DOCUMENTATION, DocumentationComponents.class);

    @Test
    public void shouldFindComponentByName() {
        ComponentProperties properties = createProperties("", "terra-action-header", "/terra-action-header/action-header/action-header");

        Component component = COMPONENTS.findComponentByName("ActionHeader");

        assertThat(component).isNotNull();
        assertThat(component.componentName).isEqualTo("ActionHeader");
        assertThat(component.properties).hasSize(1);
        assertThat(component.properties[0]).usingRecursiveComparison().isEqualTo(properties);
    }

    @Test
    public void shouldNotFindComponentByName() {
        assertThat(COMPONENTS.findComponentByName("Grid")).isNull();
    }

    @Test
    public void shouldFindPropertiesByImportPathForSingleProperty() {
        ComponentProperties property = createProperties("", "terra-action-footer", "/terra-action-footer/action-footer/standard");

        Component component = COMPONENTS.findComponentByName("ActionFooter");
        assertThat(component.properties).hasSize(1);

        ComponentProperties properties = component.findPropertiesByImportPath("terra-action-footer");
        assertThat(properties).usingRecursiveComparison().isEqualTo(property);
    }

    @Test
    public void shouldFindPropertiesByImportPathForMultipleProperties() {
        ComponentProperties property1 = createProperties("Dropdown Button", "terra-dropdown-button", "/terra-dropdown-button/dropdown-button/dropdown-button");
        ComponentProperties property2 = createProperties("List", "terra-list/lib/index", "/terra-list/list/list-item");

        Component component = COMPONENTS.findComponentByName("Item");
        assertThat(component.properties).hasSize(2);

        ComponentProperties properties1 = component.findPropertiesByImportPath("terra-dropdown-button");
        assertThat(properties1).usingRecursiveComparison().isEqualTo(property1);

        ComponentProperties properties2 = component.findPropertiesByImportPath("terra-list/lib/index");
        assertThat(properties2).usingRecursiveComparison().isEqualTo(property2);
    }

    private ComponentProperties createProperties(String family, String importPath, String relativeUrl) {
        ComponentProperties property = new ComponentProperties();
        property.family = family;
        property.importPath = importPath;
        property.relativeUrl = relativeUrl;
        return property;
    }
}
