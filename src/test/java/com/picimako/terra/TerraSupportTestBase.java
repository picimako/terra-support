//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public abstract class TerraSupportTestBase extends BasePlatformTestCase {

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }

    protected void copyFilesToProject(String... sourceFilePaths) {
        for (String path : sourceFilePaths) {
            copyFileToProject(path);
        }
    }

    protected VirtualFile copyFileToProject(String sourceFilePath) {
        return myFixture.copyFileToProject(sourceFilePath);
    }

    protected PsiFile findPsiFile(VirtualFile sourceFile) {
        return PsiManager.getInstance(getProject()).findFile(sourceFile);
    }
}
