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

    public static final String FILE_WITH_IMPORT = "import React, { useState } from 'react';\n" +
        "import Placeholder from '@cerner/terra-docs';\n" +
        "import ResponsiveElement from 'terra-responsive-element';\n" +
        "import SomeNotTerraComponent from 'not-terra-component';\n" +
        "const BreakpointExample = () => {\n" +
        "  const [breakpoint, setBreakpoint] = useState('');\n" +
        "  return (\n" +
        "    <ResponsiveElement onChange={value => setBreakpoint(value)}>\n" +
        "      <Placeholder title={breakpoint} />\n" +
        "    </ResponsiveElement>\n" +
        "    <SomeNotTerraComponent />\n" +
        "  );\n" +
        "};\n" +
        "export default BreakpointExample;";

    public static final String FILE_WITHOUT_IMPORT = "import React, { useState } from 'react';\n" +
        "import Placeholder from '@cerner/terra-docs';\n" +
        "const BreakpointExample = () => {\n" +
        "  const [breakpoint, setBreakpoint] = useState('');\n" +
        "  return (\n" +
        "    <ResponsiveElement onChange={value => setBreakpoint(value)}>\n" +
        "      <Placeholder title={breakpoint} />\n" +
        "    </ResponsiveElement>\n" +
        "  );\n" +
        "};\n" +
        "export default BreakpointExample;";

    public static final String FILE_WITHOUT_IMPORT_FROM_CLAUSE = "import React, { useState } from 'react';\n" +
        "import Placeholder from '@cerner/terra-docs';\n" +
        "import ResponsiveElement;\n" +
        "const BreakpointExample = () => {\n" +
        "  const [breakpoint, setBreakpoint] = useState('');\n" +
        "  return (\n" +
        "    <ResponsiveElement onChange={value => setBreakpoint(value)}>\n" +
        "      <Placeholder title={breakpoint} />\n" +
        "    </ResponsiveElement>\n" +
        "  );\n" +
        "};\n" +
        "export default BreakpointExample;";

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
