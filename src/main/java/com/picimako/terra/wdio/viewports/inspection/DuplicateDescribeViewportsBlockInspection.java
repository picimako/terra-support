//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getMethodExpressionOf;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getViewportsSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
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
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSFile(@NotNull JSFile file) {
                super.visitJSFile(file);

                var describeViewportsBlocks = PsiTreeUtil.collectElements(file,
                    e -> e instanceof JSExpressionStatement && isTopLevelTerraDescribeViewportsBlock(e));
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
        };
    }

    /**
     * This method caches the viewport values for each {@code Terra.describeViewports} blocks in the collection called
     * {@code viewportSets}, so that for each such block it is only once that the viewport values have to be retrieved.
     * <p>
     * Empty viewports arrays and non-array type viewports argument values are not reported.
     */
    private void reportDuplicateBlocks(PsiElement[] blocks, @NotNull ProblemsHolder holder) {
        final var blocksToReport = new HashSet<Integer>(4);
        //The maximum number of entries this will contain is the number of describeViewports blocks
        //The index of the describeViewports block - the set of viewport values
        final var viewportSets = new HashMap<Integer, Set<String>>(4);
        for (int i = 0; i < blocks.length - 1; i++) {
            viewportSets.computeIfAbsent(i, iIndex -> getViewportsSet(blocks[iIndex]));
            for (int j = i + 1; j < blocks.length; j++) {
                viewportSets.computeIfAbsent(j, jIndex -> getViewportsSet(blocks[jIndex]));
                if (!viewportSets.get(i).isEmpty() && !viewportSets.get(j).isEmpty() && viewportSets.get(i).equals(viewportSets.get(j))) {
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
        var blockMethodExpression = getMethodExpressionOf(block);
        if (blockMethodExpression != null) {
            holder.registerProblem(blockMethodExpression, TerraBundle.inspection("duplicate.describe.viewports"));
        }
    }
}
