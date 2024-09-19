//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.WDIO_SPEC_FILE_NAME_PATTERN;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import com.picimako.terra.DirectoryPsiUtil;
import com.picimako.terra.settings.TerraApplicationState;

/**
 * Folder and path handling for the Terra wdio related folders.
 */
public final class TerraWdioFolders {

    /**
     * The set of pre-defined test roots.
     * <p>
     * Public since v0.3.0.
     */
    public static final Set<String> TEST_ROOT_NAMES = Set.of("test", "tests");

    public static final String SNAPSHOTS = "__snapshots__";
    public static final String REFERENCE = "reference";
    public static final String LATEST = "latest";
    public static final String DIFF = "diff";
    public static final String REFERENCE_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + REFERENCE;
    public static final String DIFF_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + DIFF;
    private static final String LATEST_RELATIVE_PATH = "/" + SNAPSHOTS + "/" + LATEST;

    //TODO: this might be problematic with multiple projects having different wdio paths
    @Setter
    private static String wdioTestRootPath;

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
        return TerraApplicationState.getInstance().wdioRootPaths
            .stream()
            .map(path -> DirectoryPsiUtil.findDirectory(project, path.getPath()))
            .filter(Objects::nonNull)
            .findFirst()
            .map(PsiDirectory::getVirtualFile)
            .orElse(null);
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
            return TEST_ROOT_NAMES.stream()
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
        return VfsUtilCore.getRelativePath(testRoot, ProjectUtil.guessProjectDir(project));
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
     * @see #specFileIdentifier(VirtualFile, Project)
     * @since 0.3.0
     */
    @NotNull
    public static String specFolderIdentifier(VirtualFile folder, Project project) {
        String wdioRootPath = wdioRootRelativePath(project);
        String path = folder.getPath();
        return path.substring(path.indexOf(wdioRootPath) + wdioRootPath.length() + 1, path.indexOf("/" + SNAPSHOTS) + 1) + folder.getName();
    }

    /**
     * Gets the value that identifies a spec file.
     * <p>
     * The identifier is the spec file's name without the extension, concatenated to the relative path from the wdio test root.
     * So in case of the spec file at
     * <pre>
     * tests/wdio/nested/folder/some-spec.js
     * </pre>
     * the identifier will be
     * <pre>
     * nested/folder/some-spec
     * </pre>
     * In case there is no nested folder, e.g.
     * <pre>
     * tests/wdio/some-spec.js
     * </pre>
     * the result will be
     * <pre>
     * some-spec
     * </pre>
     * <p>
     * This is designed primarily for the Terra Wdio tool window, so that nested spec folder and files can be displayed
     * properly in their separate nodes.
     *
     * @param specFile the spec file to get the identifier of
     * @param project  the current project
     * @return the value identifying the file
     * @see #specFolderIdentifier(VirtualFile, Project)
     * @since 0.4.1
     */
    @NotNull
    public static String specFileIdentifier(VirtualFile specFile, Project project) {
        String wdioRootPath = wdioRootRelativePath(project);
        String path = specFile.getPath();
        return path.substring(path.indexOf(wdioRootPath) + wdioRootPath.length() + 1, path.length() - specFile.getExtension().length() - 1);
    }

    /**
     * Gets whether the argument file is under the wdio test root (directly or indirectly) in the provided project.
     *
     * @param file    the file to check the location of
     * @param project the current project
     * @return true if the file is under wdio root, false otherwise
     */
    public static boolean isInWdioFiles(VirtualFile file, Project project) {
        final var wdioRoot = projectWdioRoot(project);
        return wdioRoot != null && wdioRoot.equals(VfsUtil.getCommonAncestor(Set.of(wdioRoot, file)));
    }

    /**
     * Gets whether the argument file/folder is under the __snapshots__ directory (directly or indirectly).
     *
     * @param file the file to check the location of
     * @return true if the file is under __snapshots__ directory, false otherwise
     * @since 0.5.0
     */
    public static boolean isInSnapshotsDirectory(@Nullable VirtualFile file) {
        return file != null && file.getPath().contains(SNAPSHOTS);
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
        for (var child : directory.getChildren()) {
            if (child instanceof PsiFile) {
                String fileName = ((PsiFile) child).getName();
                if (fileName.matches(WDIO_SPEC_FILE_NAME_PATTERN)) {
                    specFiles.add((PsiFile) child);
                }
            } else if (child instanceof PsiDirectory && !isSnapshotsDirectory(((PsiDirectory) child))) {
                collectSpecFiles((PsiDirectory) child, specFiles);
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
        var images = FilenameIndex.getVirtualFilesByName(name, GlobalSearchScope.projectScope(project));
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

    @TestOnly
    public static String getWdioTestRootPath() {
        return wdioTestRootPath;
    }

    /**
     * Refreshes the argument virtual file and returns whether it still exists or not.
     */
    public static boolean existsAfterRefresh(@Nullable VirtualFile virtualFile) {
        if (virtualFile != null) {
            virtualFile.refresh(false, virtualFile.isDirectory());
            return virtualFile.exists();
        }
        return false;
    }

    private TerraWdioFolders() {
        //Utility class
    }
}
