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

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
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

    private static final Set<String> LOCATION_STRINGS = Set.of("en/chrome/huge", "en/chrome/medium");

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    //Terra.describeViewports

    public void testReferencesForTerraDescribeViewportsAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsValidatesScreenshot");
    }

    public void testReferencesForTerraDescribeViewportsAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsValidatesElement");
    }

    public void testReferencesForTerraDescribeViewportsAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsItMatchesScreenshot");
    }

    public void testReferencesForTerraDescribeViewportsAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveTerraDescribeViewportsItValidatesElement");
    }

    //describe

    public void testReferencesForDescribeAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeValidatesScreenshot");
    }

    public void testReferencesForDescribeAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeValidatesElement");
    }

    public void testReferencesForDescribeAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeItMatchesScreenshot");
    }

    public void testReferencesForDescribeAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeItValidatesElement");
    }

    //Terra.describeViewports + describe

    public void testReferencesForTerraDescribeViewportsDescribeAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeValidatesScreenshot");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeValidatesElement");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeItMatchesScreenshot");
    }

    public void testReferencesForTerraDescribeViewportsDescribeAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeViewportsDescribeItValidatesElement");
    }

    //describe + describe

    public void testReferencesForDescribeDescribeAndValidatesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeValidatesScreenshot");
    }

    public void testReferencesForDescribeDescribeAndValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeValidatesElement");
    }

    public void testReferencesForDescribeDescribeAndItMatchesScreenshot() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeItMatchesScreenshot");
    }

    public void testReferencesForDescribeDescribeAndItValidatesElement() {
        validateReferencesForSourceFile("ScreenshotResolveDescribeDescribeItValidatesElement");
    }

    //test id

    public void testScreenshotResolveDescribeValidatesElementTestId() {
        myFixture.configureByFile("tests/wdio/ScreenshotResolveDescribeValidatesElementTestId-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotResolveDescribeValidatesElementTestId-spec/terra_screenshot[single].png"));

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        TerraScreenshotPsiFile terraScreenshot = (TerraScreenshotPsiFile) element.getReferences()[0].resolve();

        assertThat(terraScreenshot.getVirtualFile().getPath())
            .isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotResolveDescribeValidatesElementTestId-spec/terra_screenshot[single].png");
    }

    public void testScreenshotResolveDescribeValidatesElementNoTestId() {
        myFixture.configureByFile("tests/wdio/ScreenshotResolveDescribeValidatesElementNoTestId-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotResolveDescribeValidatesElementNoTestId-spec/terra_screenshot[i_am_[not_)_single]].png"));

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        TerraScreenshotPsiFile terraScreenshot = (TerraScreenshotPsiFile) element.getReferences()[0].resolve();

        assertThat(terraScreenshot.getVirtualFile().getPath())
            .isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotResolveDescribeValidatesElementNoTestId-spec/terra_screenshot[i_am_[not_)_single]].png");
    }

    //single result

    public void testSingleReference() {
        myFixture.configureByFile("tests/wdio/ScreenshotResolveSingleResult-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotResolveSingleResult-spec/terra_screenshot[single].png"));

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        TerraScreenshotPsiFile terraScreenshot = (TerraScreenshotPsiFile) element.getReferences()[0].resolve();

        assertThat(terraScreenshot.getVirtualFile().getPath())
            .isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotResolveSingleResult-spec/terra_screenshot[single].png");
        assertThat(terraScreenshot.getPresentation().getLocationString()).isEqualTo("en/chrome/huge");
    }

    //Helper methods

    private void validateReferencesForSourceFile(String specName) {
        myFixture.configureByFile("tests/wdio/" + specName + "-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/" + specName + "-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/" + specName + "-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/" + specName + "-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/" + specName + "-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"));

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        ResolveResult[] resolveResults = ((PsiPolyVariantReference) element.getReferences()[0]).multiResolve(false);

        assertThat(resolveResults).hasSize(2);
        assertThat(resolvedFilePaths(specName)).containsExactlyInAnyOrder(getPath(resolveResults[0]), getPath(resolveResults[1]));
        assertThat(LOCATION_STRINGS).containsExactlyInAnyOrder(getLocationString(resolveResults[0]), getLocationString(resolveResults[1]));
    }

    private Set<String> resolvedFilePaths(String spec) {
        return Set.of(
            "/src/tests/wdio/__snapshots__/reference/en/chrome_medium/" + spec + "-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png",
            "/src/tests/wdio/__snapshots__/reference/en/chrome_huge/" + spec + "-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png"
        );
    }

    private String getPath(ResolveResult resolveResult) {
        return ((TerraScreenshotPsiFile) resolveResult.getElement()).getVirtualFile().getPath();
    }

    private String getLocationString(ResolveResult resolveResult) {
        return ((TerraScreenshotPsiFile) resolveResult.getElement()).getPresentation().getLocationString();
    }
}
