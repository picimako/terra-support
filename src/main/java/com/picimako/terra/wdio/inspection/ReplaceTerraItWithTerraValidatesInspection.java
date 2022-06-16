//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.inspection;

import static com.picimako.terra.FileTypePreconditions.isInWdioSpecFile;
import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.psi.js.JSArgumentUtil.getArgumentListOf;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.hasText;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTerraIt;

import java.util.Map;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.javascript.psi.JSBlockStatement;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * This inspection reports usage of {@code Terra.it} helpers, and provides quick fixes to replace them with their
 * {@code Terra.validates} counterparts.
 * <p>
 * All three Terra.it helpers are supported, and based on the presence of before hooks, alternative quick fixes may be
 * available to merge the particular Terra.it with the {@code before} hook before it.
 * <p>
 * The quick fix doesn't apply code reformatting at the moment, and the replacement may have an additional empty line.
 */
public class ReplaceTerraItWithTerraValidatesInspection extends TerraWdioInspectionBase {

    private static final Map<String, String> IT_TO_VALIDATES = Map.of(
        "Terra.it.validatesElement", "Terra.validates.element",
        "Terra.it.matchesScreenshot", "Terra.validates.screenshot",
        "Terra.it.isAccessible", "Terra.validates.accessibility"
    );

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTerraIt(node) && node.getExpression() instanceof JSCallExpression) {
                    var methodExpression = ((JSCallExpression) node.getExpression()).getMethodExpression();
                    if (methodExpression != null) {
                        var before = new Ref<JSExpressionStatement>();
                        if (hasPrecedingBeforeHook(node, before)) {
                            register(methodExpression, new ReplaceTerraItWithTerraValidatesQuickFix(TerraBundle.inspection("replace.terra.it.before.merged"), before.get()));
                            register(methodExpression, new ReplaceTerraItWithTerraValidatesQuickFix(TerraBundle.inspection("replace.terra.it.before.unchanged")));
                        } else {
                            register(methodExpression, new ReplaceTerraItWithTerraValidatesQuickFix(TerraBundle.inspection("replace.terra.it.simple")));
                        }
                    }
                }
            }

            private boolean hasPrecedingBeforeHook(JSExpressionStatement node, Ref<JSExpressionStatement> before) {
                JSExpressionStatement previousSibling = PsiTreeUtil.getPrevSiblingOfType(node, JSExpressionStatement.class);
                if (previousSibling != null && hasText(previousSibling, "before")) {
                    before.set(previousSibling);
                    return true;
                }
                return false;
            }

            private void register(JSExpression methodExpression, LocalQuickFix quickFix) {
                holder.registerProblem(methodExpression, TerraBundle.inspection("replace.message"), quickFix);
            }
        };
    }

    private static final class ReplaceTerraItWithTerraValidatesQuickFix implements LocalQuickFix {

        private final String quickFixName;
        private SmartPsiElementPointer<JSExpressionStatement> before;

        public ReplaceTerraItWithTerraValidatesQuickFix(String quickFixName) {
            this.quickFixName = quickFixName;
        }

        public ReplaceTerraItWithTerraValidatesQuickFix(String quickFixName, JSExpressionStatement before) {
            this.quickFixName = quickFixName;
            this.before = SmartPointerManager.getInstance(before.getProject()).createSmartPsiElementPointer(before);
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return quickFixName;
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return TerraBundle.inspection("replace.family.name");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            var terraItCall = descriptor.getPsiElement(); //e.g.: Terra.it.validatesElement
            var terraItStatement = terraItCall.getParent().getParent(); //e.g.: Terra.it.validatesElement();

            var originalArgumentList = getArgumentListOf((JSExpressionStatement) terraItStatement);
            String arguments = originalArgumentList != null && originalArgumentList.getArguments().length != 0
                ? originalArgumentList.getText() + ";" //e.g.: ('test name', {selector: '#css-selector'})
                : "();";
            //e.g.: Terra.validates.element
            var terraValidates = createJSStatement(IT_TO_VALIDATES.get(terraItCall.getText()) + arguments, terraItStatement);
            var itBlock = createJSStatement("it('INSERT TEST NAME', () => {\n" + "});", terraItStatement);

            var itBlockCallback = PsiTreeUtil.findChildOfType(itBlock, JSBlockStatement.class);
            //Add right after the opening curly braces.
            //itBlockCallback should never be null, since it is created from a fix and correct template text
            itBlockCallback.addAfter(terraValidates, itBlockCallback.getFirstChild());

            //Copy everything from the before hook to the beginning of the 'it' block
            if (before != null) {
                var beforeHookCallback = PsiTreeUtil.findChildOfType(before.getElement(), JSBlockStatement.class);
                if (beforeHookCallback != null) {
                    var children = beforeHookCallback.getChildren();
                    for (int i = children.length - 2; i >= 1; i--) { //skip copying curly braces
                        itBlockCallback.addAfter(children[i], itBlockCallback.getFirstChild());
                    }
                    before.getElement().delete();
                }
            }

            terraItStatement.replace(itBlock);
        }

        private JSExpressionStatement createJSStatement(String text, PsiElement context) {
            return JSPsiElementFactory.createJSStatement(text, context, JSExpressionStatement.class);
        }
    }
}
