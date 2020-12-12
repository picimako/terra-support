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

package com.picimako.terra.wdio.screenshot.reference;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraScreenshotReference}.
 */
public class TerraScreenshotReferenceTest extends BasePlatformTestCase {

    private static final Set<String> RESOLVED_FILE_PATHS = Set.of(
        "/src/tests/wdio/__snapshots__/reference/en/chrome_medium/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png",
        "/src/tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"
    );
    private static final Set<String> LOCATION_STRINGS = Set.of("en/chrome/huge", "en/chrome/medium");

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testReferencesForTerraDescribeViewportsAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsValidatesScreenshot-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsValidatesElement-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsItMatchesScreenshot-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsItValidatesElement-spec.js");
    }

    public void testReferencesForDescribeAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeValidatesScreenshot-spec.js");
    }

    public void testReferencesForDescribeAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeValidatesElement-spec.js");
    }

    public void testReferencesForDescribeAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeItMatchesScreenshot-spec.js");
    }

    public void testReferencesForDescribeAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeItValidatesElement-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeValidatesScreenshot-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeValidatesElement-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeItMatchesScreenshot-spec.js");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeItValidatesElement-spec.js");
    }

    public void testReferencesForDescribeDescribeAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeValidatesScreenshot-spec.js");
    }

    public void testReferencesForDescribeDescribeAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeValidatesElement-spec.js");
    }

    public void testReferencesForDescribeDescribeAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeItMatchesScreenshot-spec.js");
    }

    public void testReferencesForDescribeDescribeAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeItValidatesElement-spec.js");
    }

    public void testSingleReference() {
        configureSpecFile("ScreenshotResolveSingleResult-spec.js");
        configureSingleReferenceScreenshot();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        TerraScreenshotPsiFile terraScreenshot = (TerraScreenshotPsiFile) element.getReferences()[0].resolve();

        assertThat(terraScreenshot.getVirtualFile().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra_screenshot[single].png");
        assertThat(terraScreenshot.getPresentation().getLocationString()).isEqualTo("en/chrome/huge");
    }

    private void validateReferencesForSourceFile(String fileName) {
        configureSpecFile(fileName);
        configureMultipleReferenceScreenshots();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        ResolveResult[] resolveResults = ((PsiPolyVariantReference) element.getReferences()[0]).multiResolve(false);

        assertThat(resolveResults).hasSize(2);
        assertThat(RESOLVED_FILE_PATHS).containsExactlyInAnyOrder(getPath(resolveResults[0]), getPath(resolveResults[1]));
        assertThat(LOCATION_STRINGS).containsExactlyInAnyOrder(getLocationString(resolveResults[0]), getLocationString(resolveResults[1]));
    }

    private void configureSpecFile(String fileName) {
        myFixture.configureByFile("screenshotReferenceSpecs/" + fileName);
    }

    private void configureMultipleReferenceScreenshots() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_medium/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/diff/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/latest/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
    }

    private String getPath(ResolveResult resolveResult) {
        return ((TerraScreenshotPsiFile) resolveResult.getElement()).getVirtualFile().getPath();
    }

    private String getLocationString(ResolveResult resolveResult) {
        return ((TerraScreenshotPsiFile) resolveResult.getElement()).getPresentation().getLocationString();
    }

    private void configureSingleReferenceScreenshot() {
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra_screenshot[single].png");
    }
}
