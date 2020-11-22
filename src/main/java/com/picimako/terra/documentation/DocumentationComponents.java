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

import java.util.Arrays;

import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Object for JSON deserialization that stores Terra component documentation related information.
 *
 * @see TerraUIComponentDocumentationUrlService
 * @see TerraUIComponentDocumentationUrlProvider
 */
final class DocumentationComponents {
    private Component[] components;

    /**
     * Finds a {@link Component} by its name. If no component is found, returns null.
     *
     * @param name the component name to search by
     * @return the found component, or null if none found with the provided name
     */
    @Nullable
    Component findComponentByName(@NotNull String name) {
        return Arrays.stream(components)
                .filter(component -> name.equals(component.componentName))
                .findFirst()
                .orElse(null);
    }

    static final class Component {
        /**
         * The name of the Terra component, more precisely the XML tag name that is imported, e.g. {@code BrandFooter}
         * instead of "Brand Footer".
         */
        String componentName;

        @VisibleForTesting
        ComponentProperties[] properties;

        /**
         * Finds a {@link ComponentProperties} by its package name.
         * <p>
         * At least one ComponentProperties object should always be present, so having no properties is not handled
         * by this method.
         *
         * @param path the package name to search by
         * @return the found properties object
         */
        @NotNull
        ComponentProperties findPropertiesByImportPath(@NotNull String path) {
            return properties.length == 1
                    ? properties[0]
                    : Arrays.stream(properties).filter(prop -> path.equals(prop.importPath)).findFirst().get();
        }
    }

    static final class ComponentProperties {
        /**
         * The component family name, e.g. "Action Footer" for its component variations: Standard, Centered, Block.
         */
        String family = "";
        /**
         * The import path of the component, e.g. terra-action-footer for "Action Footer".
         */
        String importPath;
        /**
         * The relative external URL path for the Terra UI documentation. The base URL is: {@code https://engineering.cerner.com/terra-ui/components/}.
         */
        String relativeUrl;
    }
}
