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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraScreenshotValidationLineMarkerProvider}.
 */
public class TerraScreenshotValidationLineMarkerProviderTest extends BasePlatformTestCase {

    private final TerraScreenshotValidationLineMarkerProvider provider = new TerraScreenshotValidationLineMarkerProvider();

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testAddsGutterIconForExistingDefaultScreenshots() {
        myFixture.configureByFile("tests/wdio/ScreenshotResolveTerraDescribeViewportsValidatesScreenshotDefault-spec.js");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra-_screenshot--[default].png");

        PsiElement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent(), JSCallExpression.class);
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).hasSize(1);
        assertThat(collection.get(0).getLineMarkerTooltip()).isEqualTo("Navigate to related screenshots");
    }

    public void testDoesntAddGutterIconForNonExistentDefaultScreenshot() {
        myFixture.configureByFile("tests/wdio/ScreenshotResolveTerraDescribeViewportsValidatesScreenshotDefault-spec.js");

        PsiElement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent(), JSCallExpression.class);
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).isEmpty();
    }

    public void testAddsEmptyGutterIconForNonDefaultScreenshots() {
        myFixture.configureByFile("tests/wdio/ScreenshotResolveDescribeDescribeItMatchesScreenshot-spec.js");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_huge/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");
        myFixture.copyFileToProject("tests/wdio/__snapshots__/reference/en/chrome_medium/some-spec/terra-_screenshot--[with-_-replaced-_-characters_-].png");

        PsiElement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSCallExpression.class);
        List<RelatedItemLineMarkerInfo<?>> collection = new ArrayList<>();

        provider.collectNavigationMarkers(element, collection);

        assertThat(collection).hasSize(1);
        assertThat(collection.get(0).getLineMarkerTooltip()).isEqualTo("Screenshot validation happens on this line");
    }
}
