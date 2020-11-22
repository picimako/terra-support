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

package com.picimako.terra.psi.js;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.lang.ecmascript6.psi.ES6FromClause;
import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration;
import com.intellij.lang.ecmascript6.psi.ES6ImportSpecifier;
import com.intellij.lang.ecmascript6.psi.ES6ImportedBinding;
import com.intellij.lang.ecmascript6.psi.ES6NamedImports;
import com.intellij.psi.PsiElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link ES6ImportUtil}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ES6ImportUtilTest {

    private static final String RESPONSIVE_ELEMENT = "ResponsiveElement";

    @Mock
    private PsiElement psiElement;
    @Mock
    private ES6ImportDeclaration es6ImportDeclaration;
    @Mock
    private ES6NamedImports es6NamedImports;

    // getBaseComponentName

    @Test
    public void shouldReturnComponentName() {
        when(psiElement.getText()).thenReturn(RESPONSIVE_ELEMENT);

        assertThat(ES6ImportUtil.getBaseComponentName(psiElement)).isEqualTo(RESPONSIVE_ELEMENT);
    }

    @Test
    public void shouldReturnBaseComponentName() {
        when(psiElement.getText()).thenReturn("Grid.Row");

        assertThat(ES6ImportUtil.getBaseComponentName(psiElement)).isEqualTo("Grid");
    }

    // importPathFor

    @Test
    public void shouldReturnImportPathFromFromClause() {
        ES6ImportDeclaration declaration = mock(ES6ImportDeclaration.class);
        ES6FromClause fromClause = mock(ES6FromClause.class);
        when(declaration.getFromClause()).thenReturn(fromClause);
        when(fromClause.getReferenceText()).thenReturn("'terra-responsive-element'");

        assertThat(ES6ImportUtil.importPathFor(declaration)).isEqualTo("terra-responsive-element");
    }

    @Test
    public void shouldReturnImportPathFromModuleImport() {
        ES6ImportDeclaration declaration = mock(ES6ImportDeclaration.class);
        when(declaration.getFromClause()).thenReturn(null);
        when(declaration.getImportModuleText()).thenReturn("'terra-responsive-element'");

        assertThat(ES6ImportUtil.importPathFor(declaration)).isEqualTo("terra-responsive-element");
    }

    @Test
    public void shouldReturnNullWhenNoImportPathIsAvailable() {
        ES6ImportDeclaration declaration = mock(ES6ImportDeclaration.class);
        when(declaration.getFromClause()).thenReturn(null);

        assertThat(ES6ImportUtil.importPathFor(declaration)).isNull();
    }

    // isImportDeclarationForElementBase

    @Test
    public void shouldBeDeclarationWhenSingleBindingMatches() {
        ES6ImportedBinding es6ImportedBinding = mock(ES6ImportedBinding.class);
        when(es6ImportedBinding.getName()).thenReturn(RESPONSIVE_ELEMENT);

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[]{es6ImportedBinding});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isTrue();
    }

    @Test
    public void shouldBeDeclarationWhenOneOfMultipleBindingsMatch() {
        ES6ImportedBinding binding1 = mock(ES6ImportedBinding.class);
        ES6ImportedBinding binding2 = mock(ES6ImportedBinding.class);
        when(binding1.getName()).thenReturn("ContentContainer");
        when(binding2.getName()).thenReturn(RESPONSIVE_ELEMENT);

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[]{binding1, binding2});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isTrue();
    }

    @Test
    public void shouldNotBeDeclarationWhenSingleBindingDoesntMatch() {
        ES6ImportedBinding es6ImportedBinding = mock(ES6ImportedBinding.class);
        when(es6ImportedBinding.getName()).thenReturn("ContentContainer");

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[]{es6ImportedBinding});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isFalse();
    }

    @Test
    public void shouldNotBeDeclarationWhenNoneOfMultipleBindingsMatch() {
        ES6ImportedBinding binding1 = mock(ES6ImportedBinding.class);
        ES6ImportedBinding binding2 = mock(ES6ImportedBinding.class);
        when(binding1.getName()).thenReturn("ContentContainer");
        when(binding2.getName()).thenReturn("Grid");

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[]{binding1, binding2});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isFalse();
    }

    @Test
    public void shouldBeImportWhenSingleNamedImportMatches() {
        ES6ImportSpecifier es6ImportSpecifier = mock(ES6ImportSpecifier.class);
        when(es6ImportSpecifier.getName()).thenReturn(RESPONSIVE_ELEMENT);

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[0]);
        when(es6ImportDeclaration.getNamedImports()).thenReturn(es6NamedImports);
        when(es6NamedImports.getSpecifiers()).thenReturn(new ES6ImportSpecifier[]{es6ImportSpecifier});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isTrue();
    }

    @Test
    public void shouldBeImportWhenOneOfMultipleNamedImportsMatch() {
        ES6ImportSpecifier specifier1 = mock(ES6ImportSpecifier.class);
        ES6ImportSpecifier specifier2 = mock(ES6ImportSpecifier.class);
        when(specifier1.getName()).thenReturn("ContentContainer");
        when(specifier2.getName()).thenReturn(RESPONSIVE_ELEMENT);

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[0]);
        when(es6ImportDeclaration.getNamedImports()).thenReturn(es6NamedImports);
        when(es6NamedImports.getSpecifiers()).thenReturn(new ES6ImportSpecifier[]{specifier1, specifier2});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isTrue();
    }

    @Test
    public void shouldNotBeImportWhenSingleNamedImportDoesntMatch() {
        ES6ImportSpecifier es6ImportSpecifier = mock(ES6ImportSpecifier.class);
        when(es6ImportSpecifier.getName()).thenReturn("ContentContainer");

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[0]);
        when(es6ImportDeclaration.getNamedImports()).thenReturn(es6NamedImports);
        when(es6NamedImports.getSpecifiers()).thenReturn(new ES6ImportSpecifier[]{es6ImportSpecifier});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isFalse();
    }

    @Test
    public void shouldNotBeImportWhenNoneOfMultipleNamedImportsMatch() {
        ES6ImportSpecifier specifier1 = mock(ES6ImportSpecifier.class);
        ES6ImportSpecifier specifier2 = mock(ES6ImportSpecifier.class);
        when(specifier1.getName()).thenReturn("ContentContainer");
        when(specifier2.getName()).thenReturn("Grid");

        when(es6ImportDeclaration.getImportedBindings()).thenReturn(new ES6ImportedBinding[0]);
        when(es6ImportDeclaration.getNamedImports()).thenReturn(es6NamedImports);
        when(es6NamedImports.getSpecifiers()).thenReturn(new ES6ImportSpecifier[]{specifier1, specifier2});

        assertThat(ES6ImportUtil.isImportDeclarationForElementBase(es6ImportDeclaration, RESPONSIVE_ELEMENT)).isFalse();
    }
}
