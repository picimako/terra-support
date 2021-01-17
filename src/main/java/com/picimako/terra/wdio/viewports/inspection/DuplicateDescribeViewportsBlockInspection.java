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

package com.picimako.terra.wdio.viewports.inspection;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.WDIO_SPEC_FILE_NAME_PATTERN;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getMethodExpressionOf;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getViewportsSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * This inspection reports when there are multiple {@code Terra.describeViewports} blocks in a wdio spec file, with the
 * same set of viewport values. Their order doesn't have to match.
 * <p>
 * This inspection ignores files where there is no or only one such block present. For two and more than two such blocks,
 * separately, it incorporates different mechanisms to optimize performance.
 * <p>
 * This inspection will report all occurrences of duplicate describeViewports blocks.
 *
 * @since 0.4.0
 */
public class DuplicateDescribeViewportsBlockInspection extends TerraWdioInspectionBase {

    @Override
    public @NotNull String getShortName() {
        return "DuplicateDescribeViewportsBlock";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSFile(JSFile file) {
                super.visitJSFile(file);

                if (file.getName().matches(WDIO_SPEC_FILE_NAME_PATTERN)) {
                    PsiElement[] describeViewportsBlocks = PsiTreeUtil.collectElements(file, e -> e instanceof JSExpressionStatement && isTopLevelTerraDescribeViewportsBlock(e));
                    //In case of 1 or zero describeViewports blocks, there is no validation needed
                    if (describeViewportsBlocks.length == 2) {
                        if (getViewportsSet(describeViewportsBlocks[0]).equals(getViewportsSet(describeViewportsBlocks[1]))) {
                            registerProblem(describeViewportsBlocks[0], holder);
                            registerProblem(describeViewportsBlocks[1], holder);
                        }
                    } else if (describeViewportsBlocks.length > 2) {
                        reportDuplicateBlocks(describeViewportsBlocks, holder);
                    }
                }
            }
        };
    }

    /**
     * This method caches the viewport values for each {@code Terra.describeViewports} blocks in the collection called
     * {@code viewportSets}, so that for each such block it is only once that the viewport values have to be retrieved.
     */
    private void reportDuplicateBlocks(PsiElement[] blocks, @NotNull ProblemsHolder holder) {
        final Set<Integer> blocksToReport = new HashSet<>(4);
        //The maximum number of entries this will contain is the number of describeViewports blocks
        //The index of the describeViewports block - the set of viewport values
        final Map<Integer, Set<String>> viewportSets = new HashMap<>(4);
        for (int i = 0; i < blocks.length - 1; i++) {
            if (!viewportSets.containsKey(i)) {
                viewportSets.put(i, getViewportsSet(blocks[i]));
            }
            for (int j = i + 1; j < blocks.length; j++) {
                if (!viewportSets.containsKey(j)) {
                    viewportSets.put(j, getViewportsSet(blocks[j]));
                }
                if (viewportSets.get(i).equals(viewportSets.get(j))) {
                    if (!blocksToReport.contains(i)) {
                        blocksToReport.add(i);
                        registerProblem(blocks[i], holder);
                    }
                    if (!blocksToReport.contains(j)) {
                        blocksToReport.add(j);
                        registerProblem(blocks[j], holder);
                    }
                }
                if (blocksToReport.size() == blocks.length) {
                    return;
                }
            }
        }
    }

    private void registerProblem(PsiElement block, @NotNull ProblemsHolder holder) {
        JSExpression blockMethodExpression = getMethodExpressionOf(block);
        if (blockMethodExpression != null) {
            holder.registerProblem(blockMethodExpression, TerraBundle.inspection("duplicate.describe.viewports"));
        }
    }
}
