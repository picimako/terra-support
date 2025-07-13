//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiFile;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link TerraScreenshotCollector}.
 */
public class TerraScreenshotCollectorTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testCollectBasedOnTerraNameParameter() {
        configureSpecFile("CollectScreenshots-spec.js");
        configureScreenshots();

        var element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        var results = new TerraScreenshotCollector(getProject()).collectFor((JSLiteralExpression) element);

        assertThat(results).hasSize(2);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
        assertThat(((PsiFile) results[1]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    public void testCollectBasedOnTerraNameParameterAsPsiFiles() {
        configureSpecFile("CollectScreenshots-spec.js");
        configureScreenshots();

        var element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        var results = new TerraScreenshotCollector(getProject()).collectAsPsiFilesFor((JSLiteralExpression) element);

        assertThat(results).hasSize(2);
        assertThat(results[0].getVirtualFile().getPath()).contains("__snapshots__/reference");
        assertThat(results[1].getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    public void testCollectBasedOnEmptyTerraNameParameter() {
        configureSpecFile("CollectScreenshotsEmpty-spec.js");
        copyFilesToProject(
            reference("/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot[collect].png"),
            reference("/en/chrome_huge/CollectScreenshotsEmpty-spec/terra_screenshot[collect].png"),
            reference("/en/chrome_huge/CollectScreenshotsEmpty-spec/terra_screenshot[default].png"),
            reference("/en/chrome_medium/CollectScreenshotsEmpty-spec/terra_screenshot[collect].png"),
            latest("/en/chrome_huge/CollectScreenshotsEmpty-spec/terra_screenshot[collect].png"));

        var element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        var results = new TerraScreenshotCollector(getProject()).collectForDefault((JSReferenceExpression) element);

        assertThat(results).hasSize(1);
        assertThat(((PsiFile) results[0]).getVirtualFile().getPath()).contains("__snapshots__/reference");
    }

    public void testReturnsNoScreenshotForNullSourceElement() {
        assertThat(new TerraScreenshotCollector(getProject()).collectFor(null)).isEmpty();
    }

    public void testReturnsNoScreenshotForEmptyScreenshotName() {
        copyFileToProject("package.json");
        myFixture.configureByText("Collect-spec.js",
            """
                describe('terra screenshot', () => {
                    it('Test case', () => {
                        Terra.validates.screenshot(<caret>'', { selector: '#selector' });
                    });
                });""");

        var element = (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotCollector(getProject()).collectFor(element)).isEmpty();
    }

    //Helper methods

    private void configureSpecFile(String fileName) {
        myFixture.configureByFile("tests/wdio/" + fileName);
    }

    private void configureScreenshots() {
        copyFilesToProject(
            reference("/en/chrome_huge/FindUnusedScreenshot-spec/terra_screenshot[collect].png"),
            reference("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[collect].png"),
            reference("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[default].png"),
            reference("/en/chrome_medium/CollectScreenshots-spec/terra_screenshot[collect].png"),
            latest("/en/chrome_huge/CollectScreenshots-spec/terra_screenshot[collect].png"));
    }
}
