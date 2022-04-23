//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.wdio.WdioConfUtil.SELECTOR_PROPERTY_NAME;
import static com.picimako.terra.wdio.WdioConfUtil.TERRA_PROPERTY_NAME;
import static com.picimako.terra.wdio.WdioConfUtil.WDIO_CONF_JS_FILE_NAME;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Retrieves the global CSS selector define in the {@code wdio.conf.js} in the project's root directory.
 */
public class GlobalTerraSelectorRetriever {

    /**
     * Gets the global CSS selector value from the {@code terra.selector} property in wdio.conf.js. The wdio.conf.js
     * file checked is the one present in the project's root directory. It returns null if that wdio.conf.js doesn't exist.
     *
     * @param project the project to search in
     * @return the global CSS selector, or null
     */
    @Nullable
    public String getSelector(Project project) {
        PsiFile wdioConf = PsiManager.getInstance(project)
            .findDirectory(ProjectUtil.guessProjectDir(project))
            .findFile(WDIO_CONF_JS_FILE_NAME);
        return findTerraSelectorValue(wdioConf);
    }

    /**
     * Gets the {@code terra.selector} config property's value from the argument file, or null if it is not present in the file.
     * <p>
     * Ideally there should be only one terra.selector property specified, but if for some reason there are multiple
     * ones, e.g. in different config objects, then the last one's value is returned, because perhaps it is that last assignment
     * that will take effect.
     *
     * @param wdioConfJsFile the wdio.conf.js file to search in
     * @return the CSS selector defined in the terra.selector property, or null if not present
     */
    @Nullable
    private String findTerraSelectorValue(@Nullable PsiFile wdioConfJsFile) {
        List<JSProperty> terraSelectors = PsiTreeUtil.collectElementsOfType(wdioConfJsFile, JSProperty.class).stream()
            .filter(prop ->
                SELECTOR_PROPERTY_NAME.equals(prop.getName())
                    && TERRA_PROPERTY_NAME.equals(prop.getJSNamespace().getQualifiedName().getName()))
            .collect(toList());
        return !terraSelectors.isEmpty()
            ? JsonPsiUtil.stripQuotes(terraSelectors.get(terraSelectors.size() - 1).getValue().getText())
            : null;
    }
}
