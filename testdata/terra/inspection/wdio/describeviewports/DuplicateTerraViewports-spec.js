//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra */

// No-highlight cases

Terra.describeViewports('Test', [], () => {
});

Terra.describeViewports('Test', [''], () => {
});

Terra.describeViewports('Test', ['asd'], () => {
});

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['medium'], () => {
});

Terra.describeViewports('Test', ['medium', 'large', 'huge'], () => {
});

Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
});

Terra.describeViewports('Test', ['tiny', 'asd', 'medium', 'asd', 'huge', 'enormous'], () => {
});

Terra.describeViewports('Test', [large<error descr=", expected">'</error>, '<error descr=", expected">l</error>arge<error descr=", expected">'</error>, '<error descr=", expected">m</error>edium<error descr=", expected">'</error>], () => {<EOLError descr="Unclosed string literal"></EOLError>
}<error descr="Statement expected">)</error>;

// Highlight cases

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['small', 'small'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['large', 'large', 'medium'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['large', 'medium', 'medium'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['huge', 'tiny', 'huge'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['asd', 'enormous', 'asd', 'enormous', 'huge', 'enormous'], () => {
});
