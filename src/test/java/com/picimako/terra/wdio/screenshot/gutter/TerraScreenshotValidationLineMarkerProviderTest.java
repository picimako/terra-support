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

package com.picimako.terra.wdio.screenshot.gutter;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

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
        PsiElement element = configureFiles("tests/wdio/ScreenshotLineMarkersDefault-spec.js", false,
            reference("/en/chrome_huge/ScreenshotLineMarkersDefault-spec/terra_screenshot[default].png"));
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).hasSize(1);
        assertThat(collection.get(0).getLineMarkerTooltip()).isEqualTo("Navigate to related screenshots");
    }

    public void testDoesntAddGutterIconForNonExistentDefaultScreenshot() {
        PsiElement element = configureFiles("tests/wdio/ScreenshotLineMarkersDefault-spec.js", false);
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).isEmpty();
    }

    public void testDoesntAddGutterIconForParentJSExpression() {
        PsiElement element = configureFiles("tests/wdio/ScreenshotLineMarkersDefault-spec.js", true);
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).isEmpty();
    }

    public void testAddsEmptyGutterIconForNonDefaultScreenshots() {
        PsiElement element = configureFiles("tests/wdio/ScreenshotLineMarkersNonDefault-spec.js", false,
            reference("/en/chrome_huge/ScreenshotLineMarkersNonDefault-spec/terra_screenshot[nondefault].png"),
            reference("/en/chrome_medium/ScreenshotLineMarkersNonDefault-spec/terra_screenshot[nondefault].png"));
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).hasSize(1);
        assertThat(collection.get(0).getLineMarkerTooltip()).isEqualTo("Screenshot validation happens on this line");
    }

    private PsiElement configureFiles(String specPath, boolean getParent, String... screenshotPaths) {
        myFixture.configureByFile(specPath);
        for (String path : screenshotPaths) {
            myFixture.copyFileToProject(path);
        }
        PsiElement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSCallExpression.class);
        return getParent ? element.getParent() : element;
    }
}
