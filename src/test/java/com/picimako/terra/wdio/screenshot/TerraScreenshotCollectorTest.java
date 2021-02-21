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

package com.picimako.terra.wdio.screenshot;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraScreenshotCollector}.
 */
public class TerraScreenshotCollectorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testCollectBasedOnTerraNameParameter() {
        configureSpecFile("CollectScreenshots-spec.js");
        configureScreenshots();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiElement[] results = new TerraScreenshotCollector(getProject()).collectFor((JSLiteralExpression) element);

        assertThat(results).hasSize(2);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
        assertThat(((PsiFile) results[1]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    public void testCollectBasedOnTerraNameParameterAsPsiFiles() {
        configureSpecFile("CollectScreenshots-spec.js");
        configureScreenshots();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiElement[] results = new TerraScreenshotCollector(getProject()).collectAsPsiFilesFor((JSLiteralExpression) element);

        assertThat(results).hasSize(2);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
        assertThat(((PsiFile) results[1]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    public void testCollectBasedOnEmptyTerraNameParameter() {
        configureSpecFile("CollectScreenshotsEmpty-spec.js");
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/CollectScreenshotsEmpty-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/CollectScreenshotsEmpty-spec/terra_screenshot[default].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/CollectScreenshotsEmpty-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/CollectScreenshotsEmpty-spec/terra_screenshot[collect].png"));

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiElement[] results = new TerraScreenshotCollector(getProject()).collectForDefault((JSReferenceExpression) element);

        assertThat(results).hasSize(1);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    //Helper methods

    private void configureSpecFile(String fileName) {
        myFixture.configureByFile("tests/wdio/" + fileName);
    }

    private void configureScreenshots() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[default].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/CollectScreenshots-spec/terra_screenshot[collect].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[collect].png"));
    }
}
