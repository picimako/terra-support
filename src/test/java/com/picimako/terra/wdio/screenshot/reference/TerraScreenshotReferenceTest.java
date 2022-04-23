//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.reference;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link TerraScreenshotReference}.
 */
public class TerraScreenshotReferenceTest extends TerraToolkitTestCase {

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
        TerraScreenshotPsiFile terraScreenshot = configureTestIdOrSingleReferenceFiles("tests/wdio/ScreenshotResolveDescribeValidatesElementTestId-spec.js",
            reference("/en/chrome_huge/ScreenshotResolveDescribeValidatesElementTestId-spec/terra_screenshot[single].png"));

        assertThat(terraScreenshot.getVirtualFile().getPath())
            .isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotResolveDescribeValidatesElementTestId-spec/terra_screenshot[single].png");
    }

    public void testScreenshotResolveDescribeValidatesElementNoTestId() {
        TerraScreenshotPsiFile terraScreenshot = configureTestIdOrSingleReferenceFiles("tests/wdio/ScreenshotResolveDescribeValidatesElementNoTestId-spec.js",
            reference("/en/chrome_huge/ScreenshotResolveDescribeValidatesElementNoTestId-spec/terra_screenshot[i_am_[not_)_single]].png"));

        assertThat(terraScreenshot.getVirtualFile().getPath())
            .isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotResolveDescribeValidatesElementNoTestId-spec/terra_screenshot[i_am_[not_)_single]].png");
    }

    //single result

    public void testSingleReference() {
        TerraScreenshotPsiFile terraScreenshot = configureTestIdOrSingleReferenceFiles("tests/wdio/ScreenshotResolveSingleResult-spec.js",
            reference("/en/chrome_huge/ScreenshotResolveSingleResult-spec/terra_screenshot[single].png"));

        assertThat(terraScreenshot.getVirtualFile().getPath())
            .isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotResolveSingleResult-spec/terra_screenshot[single].png");
        assertThat(terraScreenshot.getPresentation().getLocationString()).isEqualTo("en/chrome/huge");
    }

    //sort order

    public void testSuggestionsAreSortedAlphabetically() {
        myFixture.configureByFile("tests/wdio/Alphabetical-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/Alphabetical-spec/alpha[betical].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/Alphabetical-spec/alpha[betical].png"));
        myFixture.copyFileToProject(reference("/en/chrome_enormous/Alphabetical-spec/alpha[betical].png"));

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        ResolveResult[] resolveResults = ((PsiPolyVariantReference) element.getReferences()[0]).multiResolve(false);

        assertThat(resolveResults)
            .extracting(result -> (TerraScreenshotPsiFile) result.getElement())
            .extracting(e -> e.getPresentation().getLocationString())
            .containsExactly("en/chrome/enormous", "en/chrome/huge", "en/chrome/medium");
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

    private TerraScreenshotPsiFile configureTestIdOrSingleReferenceFiles(String specPath, String screenshotPath) {
        myFixture.configureByFile(specPath);
        myFixture.copyFileToProject(screenshotPath);

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        return (TerraScreenshotPsiFile) element.getReferences()[0].resolve();
    }
}
