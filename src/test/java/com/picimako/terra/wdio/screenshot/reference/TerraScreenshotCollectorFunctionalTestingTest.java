//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.reference;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightProjectDescriptor;

import com.picimako.terra.TerraFunctionalTestingTestCase;
import com.picimako.terra.wdio.screenshot.TerraScreenshotCollector;

/**
 * Unit test for {@link TerraScreenshotCollector}.
 */
public class TerraScreenshotCollectorFunctionalTestingTest extends TerraFunctionalTestingTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/functestroot";
    }

    public void testCollectBasedOnTerraNameParameter() {
        configureFiles();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiElement[] results = new TerraScreenshotCollector(getProject()).collectFor((JSLiteralExpression) element);

        assertThat(results).hasSize(2);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
        assertThat(((PsiFile) results[1]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    public void testCollectBasedOnTerraNameParameterAsPsiFiles() {
        configureFiles();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiElement[] results = new TerraScreenshotCollector(getProject()).collectAsPsiFilesFor((JSLiteralExpression) element);

        assertThat(results).hasSize(2);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
        assertThat(((PsiFile) results[1]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    private void configureFiles() {
        myFixture.configureByFile("tests/wdio/CollectScreenshots-spec.js");
        myFixture.copyFileToProject("package.json");

        myFixture.copyFileToProject(reference("/terra-default-theme/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot.png"));
        myFixture.copyFileToProject(reference("/terra-default-theme/en/chrome_huge/CollectScreenshots-spec/terra_screenshot.png"));
        myFixture.copyFileToProject(reference("/clinical-lowlight-theme/en/chrome_medium/CollectScreenshots-spec/terra_screenshot.png"));
        myFixture.copyFileToProject(latest("/clinical-lowlight-theme/en/chrome_huge/CollectScreenshots-spec/terra_screenshot.png"));
    }

    //To fix the test failure when copying package.json to the project
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
