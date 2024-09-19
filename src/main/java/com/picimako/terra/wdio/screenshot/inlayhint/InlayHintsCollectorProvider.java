//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inlayhint;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.SELECTOR;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getScreenshotValidationProperty;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.settings.TerraApplicationState;
import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.screenshot.inspection.GlobalTerraSelectorRetriever;

/**
 * Provides the functionality for {@code com.intellij.codeInsight.hints.InlayHintsProvider#getCollectorFor()}.
 * <p>
 * It is implemented in Java, because the Kotlin implementation had some issues during compilation.
 */
@SuppressWarnings("UnstableApiUsage")
public final class InlayHintsCollectorProvider {

    private static final String SCREENSHOT_HINT_LABEL = "screenshot: ";
    private static final String SELECTOR_HINT_LABEL = "selector: ";

    public static InlayHintsCollector getCollectorFor(@NotNull PsiFile file, @NotNull Editor editor, @NotNull TerraScreenshotInlayHintsProvider.Settings settings, @NotNull InlayHintsSink sink) {
        return new FactoryInlayHintsCollector(editor) {
            @Override
            public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
                if (TerraResourceManager.isUsingTerra(element.getProject()) && !file.getProject().getService(DumbService.class).isDumb()
                    && element instanceof JSCallExpression
                    && isScreenshotValidationCall((JSCallExpression) element)) {
                    var addedInlineScreenshot = false;
                    if (!TerraApplicationState.getInstance().showScreenshotName.equals(InlayType.Disabled.name())) {
                        addedInlineScreenshot = addScreenshotHint((JSCallExpression) element, addedInlineScreenshot);
                    }
                    if (!TerraApplicationState.getInstance().showCssSelector.equals(InlayType.Disabled.name())) {
                        addCSSSelectorHint((JSCallExpression) element, addedInlineScreenshot);
                    }
                }
                return true;
            }

            private boolean addScreenshotHint(JSCallExpression element, boolean addedInlineScreenshot) {
                var isAddedInlineScreenshot = addedInlineScreenshot;
                final var nameExpr = getFirstArgumentAsStringLiteral(element.getArgumentList());
                final var nameResolver = TerraResourceManager.getInstance(element.getProject()).screenshotNameResolver();
                final var screenshotName = nameResolver.resolveWithFallback(nameExpr, element.getMethodExpression());
                switch (TerraApplicationState.getInstance().showScreenshotName) {
                    case "Inline":
                        addInlineHint(element, SCREENSHOT_HINT_LABEL, screenshotName);
                        isAddedInlineScreenshot = true;
                        break;
                    case "Block":
                        addBlockHint(element, SCREENSHOT_HINT_LABEL, screenshotName);
                        break;
                    default:
                }
                return isAddedInlineScreenshot;
            }

            private void addCSSSelectorHint(JSCallExpression element, boolean addedInlineScreenshot) {
                final var selectorProperty = getScreenshotValidationProperty(element, SELECTOR);
                if (selectorProperty == null) {
                    String selector = GlobalTerraSelectorRetriever.getInstance(element.getProject()).getSelector();
                    final var cssSelector = selector != null ? selector : "";
                    if (!cssSelector.isEmpty()) {
                        switch (TerraApplicationState.getInstance().showCssSelector) {
                            case "Inline":
                                addInlineHint(element, addedInlineScreenshot ? ", " + SELECTOR_HINT_LABEL : SELECTOR_HINT_LABEL, cssSelector);
                                break;
                            case "Block":
                                addBlockHint(element, SELECTOR_HINT_LABEL, cssSelector);
                                break;
                            default:
                        }
                    }
                }
            }

            private void addInlineHint(JSCallExpression element, String label, String value) {
                sink.addInlineElement(element.getParent().getTextRange().getStartOffset() + element.getParent().getTextLength(),
                    true,
                    basePresentation(label, value),
                    false
                );
            }

            private void addBlockHint(JSCallExpression element, String label, String value) {
                final var width = EditorUtil.getPlainSpaceWidth(editor);
                //Starting from 2022.3 there doesn't seem to be a document associated with the preview's dummy file,
                // so for now, there is no preview displayed for block type hints
                final var document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
                if (document == null) return;

                final var line = document.getLineNumber(element.getParent().getTextRange().getStartOffset());
                final var startOffset = document.getLineStartOffset(line);
                final var leftInset = (element.getTextRange().getStartOffset() - startOffset) * width;

                final var insetPres = getFactory().inset(basePresentation(label, value), leftInset, 0, 0, 0);
                sink.addBlockElement(element.getParent().getTextRange().getStartOffset(), true, true, 0, insetPres);
            }

            private InlayPresentation basePresentation(String label, String value) {
                return getFactory().seq(getFactory().smallText(label), getFactory().smallText(value));
            }
        };
    }

    private InlayHintsCollectorProvider() {
    }
}
