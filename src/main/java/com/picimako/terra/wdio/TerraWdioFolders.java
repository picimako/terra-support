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

package com.picimako.terra.wdio;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Folder and path handling for the Terra wdio related folders.
 */
public final class TerraWdioFolders {

    /**
     * The set of pre-defined wdio test roots. If a project happens to use a different root, there are two ways to support it.
     * Either rename that folder to one of the pre-defined ones here, or add that folder to this set of roots, but the first option is preferred.
     */
    private static final Set<String> WDIO_TEST_ROOT_NAMES = Set.of("test", "tests");

    public static final String SNAPSHOTS = "__snapshots__";
    public static final String REFERENCE = "reference";
    public static final String LATEST = "latest";
    public static final String DIFF = "diff";
    public static final String REFERENCE_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + REFERENCE;
    public static final String DIFF_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + DIFF;
    private static final String LATEST_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + LATEST;

    private static String wdioTestRootPath;

    /**
     * Gets the VirtualFile representing the wdio tests root folder in the project, or null if there is no recognizable
     * tests root.
     *
     * @param project the current project
     * @return the virtual file for the wdio root folder, or null if none is recognized
     */
    @Nullable
    public static VirtualFile projectWdioRoot(Project project) {
        final VirtualFile projectDirectory = ProjectUtil.guessProjectDir(project);
        if (projectDirectory != null && projectDirectory.exists()) {
            return WDIO_TEST_ROOT_NAMES.stream()
                    .map(projectDirectory::findChild)
                    .filter(Objects::nonNull)
                    .map(testDir -> testDir.findChild("wdio"))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Collect spec folders within the provided Terra wdio test root, and within that inside the provided {@code imageType} folder
     * (diff, latest or reference), and return it as a {@link Stream} for further manipulation.
     *
     * @param imageType                 the image type folder to collect the spec folders from
     * @param filesAndFoldersInWdioRoot the list of files and folders inside the wdio test root
     * @return the stream of virtual files for the necessary spec folders
     */
    @NotNull
    public static Stream<VirtualFile> collectSpecFoldersInside(@NotNull String imageType, @NotNull List<VirtualFile> filesAndFoldersInWdioRoot) {
        return filesAndFoldersInWdioRoot.stream()
                .filter(item -> TerraWdioFolders.SNAPSHOTS.equals(item.getName())) //this should result in only one folder
                .flatMap(snapshots -> Arrays.stream(VfsUtil.getChildren(snapshots))) //diff, latest, reference
                .filter(type -> imageType.equals(type.getName())) //reference or latest or diff
                .flatMap(reference -> Arrays.stream(VfsUtil.getChildren(reference))) //en, en-US, es, ...
                .flatMap(locale -> Arrays.stream(VfsUtil.getChildren(locale))) //chrome_enormous, chrome_tiny, ...
                .flatMap(browserViewport -> Arrays.stream(VfsUtil.getChildren(browserViewport))); //spec file folders
    }

    /**
     * Gets whether the argument file/folder is under the wdio test root (directly or indirectly) in the provided project.
     *
     * @param file    the file to check the location of
     * @param project the current project
     * @return true if the file is under wdio root, false otherwise
     */
    public static boolean isInWdioFiles(VirtualFile file, Project project) {
        final VirtualFile wdioRoot = projectWdioRoot(project);
        return wdioRoot != null && wdioRoot.equals(VfsUtil.getCommonAncestor(Set.of(wdioRoot, file)));
    }

    /**
     * Gets whether the argument virtual file is located inside a {@code diff} folder.
     *
     * @param file    the file to check the location of
     * @param project the project
     * @return true is the file is located inside a diff folder, false otherwise
     */
    public static boolean isDiffScreenshot(VirtualFile file, @NotNull Project project) {
        return isInWdioFiles(file, project) && file.getPath().contains(DIFF_RELATIVE_PATH);
    }

    /**
     * Gets whether the argument virtual file is located inside a {@code latest} folder.
     *
     * @param file    the file to check the location of
     * @param project the project
     * @return true is the file is located inside a latest folder, false otherwise
     */
    public static boolean isLatestScreenshot(VirtualFile file, @NotNull Project project) {
        return isInWdioFiles(file, project) && file.getPath().contains(LATEST_RELATIVE_PATH);
    }

    /**
     * Gets whether the argument virtual file is located inside a {@code reference} folder.
     *
     * @param file    the file to check the location of
     * @param project the project
     * @return true is the file is located inside a reference folder, false otherwise
     */
    public static boolean isReferenceScreenshot(VirtualFile file, @NotNull Project project) {
        return isInWdioFiles(file, project) && file.getPath().contains(REFERENCE_RELATIVE_PATH);
    }

    /**
     * Gets the diff screenshot file that is located at the same relative path as the argument latest image path.
     *
     * @param project the project
     * @param latest  the latest version of an image
     * @return the virtual file containing the diff image
     */
    @Nullable
    public static VirtualFile diffImageForLatest(Project project, VirtualFile latest) {
        return getMatchingImageForName(latest.getName(), getDiffPathForLatestPath(latest.getPath()), project);
    }

    private static String getDiffPathForLatestPath(String latestPath) {
        return latestPath.replace(latestPath(), diffPath());
    }

    /**
     * Gets the latest screenshot file that is located at the same relative path as the argument reference image path.
     *
     * @param project   the project
     * @param reference the reference version of an image
     * @return the virtual file containing the latest image
     */
    @Nullable
    public static VirtualFile latestImageForReference(Project project, VirtualFile reference) {
        return getMatchingImageForName(reference.getName(), getLatestPathForReferencePath(reference.getPath()), project);
    }

    private static String getLatestPathForReferencePath(String referencePath) {
        return referencePath.replace(referencePath(), latestPath());
    }

    private static VirtualFile getMatchingImageForName(String name, String desiredPath, Project project) {
        Collection<VirtualFile> images = FilenameIndex.getVirtualFilesByName(project, name, GlobalSearchScope.projectScope(project));
        return images.stream()
                .filter(image -> image.getPath().equals(desiredPath))
                .findFirst()
                .orElse(null);
    }

    public static String referencePath() {
        return wdioTestRootPath + REFERENCE_RELATIVE_PATH;
    }

    public static String latestPath() {
        return wdioTestRootPath + LATEST_RELATIVE_PATH;
    }

    public static String diffPath() {
        return wdioTestRootPath + DIFF_RELATIVE_PATH;
    }

    public static void setWdioTestRootPath(String path) {
        wdioTestRootPath = path;
    }

    private TerraWdioFolders() {
        //Utility class
    }
}
