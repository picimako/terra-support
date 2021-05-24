/*
 * Copyright 2021 Tamás Balog
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

package com.picimako.terra.wdio;

import java.util.Optional;

import com.intellij.javascript.nodejs.PackageJsonData;
import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager;
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;

/**
 * Base manager for Terra resource handlers.
 */
public abstract class TerraResourceManager implements TerraResourceHandlerProvider {

    private static final String CERNER_TERRA_FUNCTIONAL_TESTING = "@cerner/terra-functional-testing";

    /**
     * Creates a resource manager based on the Terra test library used in the project.
     * <p>
     * Whether terra-functional-testing or terra-toolkit is used is determined by whether {@code @cerner/terra-functional-testing}
     * or the {@code terra-toolkit} package is included in the dependencies of the package.json in the project's root.
     * If, for some reason, both are included, terra-functional-testing takes precedence.
     * <p>
     * {@code @cerner/terra-cli} is not taken into account when determining the used test library.
     * <p>
     * The resource manager instance is cached and is recreated whenever the root package.json file changes.
     */
    public static TerraResourceManager getInstance(Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> {
            Object[] dependencies = new Object[1];
            Optional<PackageJsonData> rootPackageJson = PackageJsonFileManager.getInstance(project).getValidPackageJsonFiles().stream()
                .filter(packageJson -> {
                    VirtualFile projectDir = ProjectUtil.guessProjectDir(project);
                    if (projectDir != null && packageJson.getParent().getPath().equals(projectDir.getPath())) {
                        dependencies[0] = packageJson;
                        return true;
                    }
                    return false;
                })
                .map(PackageJsonUtil::getOrCreateData)
                .findFirst();

            if (dependencies[0] != null) {
                if (rootPackageJson.isPresent() && rootPackageJson.get().containsOneOfDependencyOfAnyType(CERNER_TERRA_FUNCTIONAL_TESTING)) {
                    return CachedValueProvider.Result.create(project.getService(TerraFunctionalTestingManager.class), dependencies[0]);
                }
                return CachedValueProvider.Result.create(project.getService(TerraToolkitManager.class), dependencies[0]);
            }
            //This will happen when there is no package.json in the project root
            return CachedValueProvider.Result.create(project.getService(TerraToolkitManager.class), ModificationTracker.NEVER_CHANGED);
        });
    }

    public static boolean isUsingTerraToolkit(Project project) {
        return getInstance(project) instanceof TerraToolkitManager;
    }
}
