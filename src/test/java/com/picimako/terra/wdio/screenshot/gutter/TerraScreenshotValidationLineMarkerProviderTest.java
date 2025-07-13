//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.gutter;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link TerraScreenshotValidationLineMarkerProvider}.
 */
public class TerraScreenshotValidationLineMarkerProviderTest extends TerraToolkitTestCase {

    private final TerraScreenshotValidationLineMarkerProvider provider = new TerraScreenshotValidationLineMarkerProvider();

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testAddsGutterIconForExistingDefaultScreenshots() {
        var element = configureFiles("tests/wdio/ScreenshotLineMarkersDefault-spec.js", false,
            reference("/en/chrome_huge/ScreenshotLineMarkersDefault-spec/terra_screenshot[default].png"));
        var collection = new ArrayList<RelatedItemLineMarkerInfo<?>>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).hasSize(1);
        assertThat(collection.getFirst().getLineMarkerTooltip()).isEqualTo("Navigate to related screenshots");
    }

    public void testDoesntAddGutterIconForNonExistentDefaultScreenshot() {
        var element = configureFiles("tests/wdio/ScreenshotLineMarkersDefault-spec.js", false);
        var collection = new ArrayList<RelatedItemLineMarkerInfo<?>>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).isEmpty();
    }

    public void testDoesntAddGutterIconForParentJSExpression() {
        var element = configureFiles("tests/wdio/ScreenshotLineMarkersDefault-spec.js", true);
        var collection = new ArrayList<RelatedItemLineMarkerInfo<?>>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).isEmpty();
    }

    public void testAddsEmptyGutterIconForNonDefaultScreenshots() {
        var element = configureFiles("tests/wdio/ScreenshotLineMarkersNonDefault-spec.js", false,
            reference("/en/chrome_huge/ScreenshotLineMarkersNonDefault-spec/terra_screenshot[nondefault].png"),
            reference("/en/chrome_medium/ScreenshotLineMarkersNonDefault-spec/terra_screenshot[nondefault].png"));
        var collection = new ArrayList<RelatedItemLineMarkerInfo<?>>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).hasSize(1);
        assertThat(collection.getFirst().getLineMarkerTooltip()).isEqualTo("Screenshot validation happens on this line");
    }

    private PsiElement configureFiles(String specPath, boolean getParent, String... screenshotPaths) {
        myFixture.configureByFile(specPath);
        for (String path : screenshotPaths) {
            copyFileToProject(path);
        }
        var element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSCallExpression.class);
        return getParent ? element.getParent() : element;
    }
}
