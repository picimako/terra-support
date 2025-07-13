//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

/**
 * Provides constants and methods for working with JavaScript and React JS files in unit tests.
 * <p>
 * Code snippets are based on the
 * <a href="https://engineering.cerner.com/terra-ui/components/terra-responsive-element/responsive-element/responsive-element">
 *  Terra UI Responsive Element</a> documentation.
 */
public final class JavaScriptTestFileSupport {

    public static final String FILE_WITH_IMPORT = """
        import React, { useState } from 'react';
        import Placeholder from '@cerner/terra-docs';
        import ResponsiveElement from 'terra-responsive-element';
        import SomeNotTerraComponent from 'not-terra-component';
        const BreakpointExample = () => {
          const [breakpoint, setBreakpoint] = useState('');
          return (
            <ResponsiveElement onChange={value => setBreakpoint(value)}>
              <Placeholder title={breakpoint} />
            </ResponsiveElement>
            <SomeNotTerraComponent />
          );
        };
        export default BreakpointExample;""";

    public static final String FILE_WITHOUT_IMPORT = """
        import React, { useState } from 'react';
        import Placeholder from '@cerner/terra-docs';
        const BreakpointExample = () => {
          const [breakpoint, setBreakpoint] = useState('');
          return (
            <ResponsiveElement onChange={value => setBreakpoint(value)}>
              <Placeholder title={breakpoint} />
            </ResponsiveElement>
          );
        };
        export default BreakpointExample;""";

    public static final String FILE_WITHOUT_IMPORT_FROM_CLAUSE = """
        import React, { useState } from 'react';
        import Placeholder from '@cerner/terra-docs';
        import ResponsiveElement;
        const BreakpointExample = () => {
          const [breakpoint, setBreakpoint] = useState('');
          return (
            <ResponsiveElement onChange={value => setBreakpoint(value)}>
              <Placeholder title={breakpoint} />
            </ResponsiveElement>
          );
        };
        export default BreakpointExample;""";

    /**
     * Creates a {@link PsiFile} object from the argument file content.
     *
     * @param project     the project
     * @param fileContent the file content to create the PSI file from
     * @return the PSI file object
     */
    public static PsiFile createJavaScriptFileFromText(Project project, String fileContent) {
        return PsiFileFactory.getInstance(project).createFileFromText(JavascriptLanguage.INSTANCE, fileContent);
    }

    private JavaScriptTestFileSupport() {
        //Utility class
    }
}
