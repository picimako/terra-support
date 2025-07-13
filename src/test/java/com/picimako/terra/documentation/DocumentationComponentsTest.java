//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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

    private static final String DOCUMENTATION = """
        {
          "components": [
            {
              "componentName": "ActionFooter",
              "properties": [
                {
                  "importPath": "terra-action-footer",
                  "relativeUrl": "/terra-action-footer/action-footer/standard"
                }
              ]
            },
            {
              "componentName": "ActionHeader",
              "properties": [
                {
                  "importPath": "terra-action-header",
                  "relativeUrl": "/terra-action-header/action-header/action-header"
                }
              ]
            },
            {
              "componentName": "Item",
              "properties": [
                {
                  "family": "Dropdown Button",
                  "importPath": "terra-dropdown-button",
                  "relativeUrl": "/terra-dropdown-button/dropdown-button/dropdown-button"
                },
                {
                  "family": "List",
                  "importPath": "terra-list/lib/index",
                  "relativeUrl": "/terra-list/list/list-item"
                }
              ]
            }
          ]
        }""";
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
