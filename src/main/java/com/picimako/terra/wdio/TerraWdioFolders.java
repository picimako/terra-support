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

import static com.picimako.terra.wdio.TerraWdioPsiUtil.WDIO_SPEC_FILE_NAME_PATTERN;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

/**
 * Folder and path handling for the Terra wdio related folders.
 */
public final class TerraWdioFolders {

    /**
     * The set of pre-defined wdio test roots. If a project happens to use a different root, there are two ways to support it.
     * Either rename that folder to one of the pre-defined ones here, or add that folder to this set of roots, but the first option is preferred.
     * <p>
     * Public since v0.3.0.
     */
    public static final Set<String> WDIO_TEST_ROOT_NAMES = Set.of("test", "tests");

    public static final String SNAPSHOTS = "__snapshots__";
    public static final String REFERENCE = "reference";
    public static final String LATEST = "latest";
    public static final String DIFF = "diff";
    public static final String REFERENCE_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + REFERENCE;
    public static final String DIFF_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + DIFF;
    private static final String LATEST_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + LATEST;

    private static String wdioTestRootPath;
    private static CachedValue<VirtualFile> cachedWdioRoot;

    /**
     * Gets the VirtualFile representing the wdio tests root folder in the project, or null if there is no recognizable
     * tests root.
     * <p>
     * This value is also cached to improve performance.
     *
     * @param project the current project
     * @return the virtual file for the wdio root folder, or null if none is recognized
     */
    @Nullable
    public static VirtualFile projectWdioRoot(Project project) {
        if (cachedWdioRoot == null) {
            cachedWdioRoot = CachedValuesManager.getManager(project).createCachedValue(() -> new CachedValueProvider.Result<>(getTestRoot(project, "wdio"), ModificationTracker.NEVER_CHANGED));
        }
        return cachedWdioRoot.getValue();
    }

    /**
     * Gets the VirtualFile representing the test root folder for the provided name in the project,
     * or null if there is no recognizable tests root.
     *
     * @param project the current project
     * @return the virtual file for the desired test root folder, or null if none is recognized
     * @since 0.3.0
     */
    @Nullable
    public static VirtualFile getTestRoot(Project project, String testFolderName) {
        final VirtualFile projectDirectory = ProjectUtil.guessProjectDir(project);
        if (projectDirectory != null && projectDirectory.exists()) {
            return WDIO_TEST_ROOT_NAMES.stream()
                .map(projectDirectory::findChild)
                .filter(Objects::nonNull)
                .map(testDir -> testDir.findChild(testFolderName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    /**
     * Gets the relative path of the wdio test root folder within the project root.
     *
     * @param project the current project
     * @return the relative path of the wdio test root
     * @since 0.3.0
     */
    public static String wdioRootRelativePath(@NotNull Project project) {
        return getRelativePathToProjectDir(project, projectWdioRoot(project));
    }

    /**
     * Gets the relative path of the test root folder within the project root.
     *
     * @param project  the current project
     * @param testRoot the test root
     * @return the relative path of test root
     * @since 0.3.0
     */
    public static String getRelativePathToProjectDir(@NotNull Project project, @NotNull VirtualFile testRoot) {
        return VfsUtil.getRelativePath(testRoot, ProjectUtil.guessProjectDir(project));
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
            .filter(VirtualFile::isDirectory)
            .filter(dir -> dir.getName().endsWith("-spec"))
            .filter(dir -> imageType.equals(dir.getParent().getParent().getParent().getName()));
    }

    /**
     * Gets the value that identifies a spec folder within a {@code __snapshots__} folder.
     * <p>
     * The identifier is the spec folder's name concatenated to the relative path from the wdio test root.
     * So in case of the spec folder at
     * <pre>
     * tests/wdio/nested/folder/__snapshots__/en/chrome_huge/some-spec
     * </pre>
     * the identifier will be
     * <pre>
     * nested/folder/some-spec
     * </pre>
     * In case there is no nested folder, e.g.
     * <pre>
     * tests/wdio/__snapshots__/en/chrome_huge/some-spec
     * </pre>
     * the result will be
     * <pre>
     * some-spec
     * </pre>
     * <p>
     * This is designed primarily for the Terra Wdio tool window, so that nested spec folder and files can be displayed
     * properly in their separate nodes.
     *
     * @param folder  the spec folder to get the identifier of
     * @param project the current project
     * @return the value identifying the folder
     * @since 0.3.0
     */
    public static String specFolderIdentifier(VirtualFile folder, Project project) {
        String wdioRootPath = wdioRootRelativePath(project);
        String path = folder.getPath();
        return path.substring(path.indexOf(wdioRootPath) + wdioRootPath.length() + 1, path.indexOf("/" + SNAPSHOTS) + 1) + folder.getName();
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
     * Collects all spec files from the given directory to the argument list.
     * <p>
     * It also takes into account that embedded spec files and screenshots may be present too.
     * <p>
     * __snapshots__ folders are excluded because they are not supposed to contain spec files, only screenshots.
     *
     * @param directory the directory to collect the spec files in
     * @param specFiles the collection to store the found files in
     * @since 0.3.0
     */
    public static void collectSpecFiles(PsiDirectory directory, List<PsiFile> specFiles) {
        for (PsiElement element : directory.getChildren()) {
            if (element instanceof PsiFile) {
                String fileName = ((PsiFile) element).getName();
                if (fileName.matches(WDIO_SPEC_FILE_NAME_PATTERN)) {
                    specFiles.add((PsiFile) element);
                }
            } else if (element instanceof PsiDirectory && !isSnapshotsDirectory(((PsiDirectory) element))) {
                collectSpecFiles((PsiDirectory) element, specFiles);
            }
        }
    }

    /**
     * Collects the spec files (nested ones as well) from the argument list of all files and folder within the wdio root folder.
     *
     * @param filesAndFoldersInWdioRoot files and folder in the wdio root folder
     * @return the set of virtual files for all spec files
     */
    public static Set<VirtualFile> collectSpecFiles(@NotNull List<VirtualFile> filesAndFoldersInWdioRoot) {
        return filesAndFoldersInWdioRoot.stream()
            .filter(virtualFile -> !virtualFile.isDirectory())
            .filter(virtualFile -> virtualFile.getName().matches(WDIO_SPEC_FILE_NAME_PATTERN))
            .collect(toSet());
    }

    /**
     * Gets whether the argument directory is a __snapshots__ folder.
     *
     * @param directory the directory to validate the name of
     * @return true if the directory is a snapshots one, false otherwise
     * @since 0.3.0
     */
    public static boolean isSnapshotsDirectory(PsiDirectory directory) {
        return SNAPSHOTS.equals(directory.getName());
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
     * <p>
     * If it's enough to check the path of the file because a check somewhere else already makes sure that the file
     * is in the wdio test files, you can use {@link #isReferenceScreenshot(VirtualFile)}.
     *
     * @param file    the file to check the location of
     * @param project the project
     * @return true is the file is located inside a reference folder, false otherwise
     */
    public static boolean isReferenceScreenshot(VirtualFile file, @NotNull Project project) {
        return isInWdioFiles(file, project) && isReferenceScreenshot(file);
    }

    /**
     * Gets whether the argument virtual file is located inside a {@code reference} folder.
     * <p>
     * If it's also needed to validate whether the file is actually within the wdio test files, please use
     * {@link #isReferenceScreenshot(VirtualFile, Project)} instead.
     *
     * @param file the file to check the location of
     * @return true is the file is located inside a reference folder, false otherwise
     */
    public static boolean isReferenceScreenshot(VirtualFile file) {
        return file.getPath().contains(REFERENCE_RELATIVE_PATH);
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

    /**
     * Should be called only from test code.
     */
    @TestOnly
    public static void clearWdioRootCache() {
        cachedWdioRoot = null;
    }

    private TerraWdioFolders() {
        //Utility class
    }
}
