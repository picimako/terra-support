//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra */

// No-highlight cases

Terra.describeViewports('Test', [], () => {
});

Terra.describeViewports('Test', [''], () => {
});

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['small', 'huge', 'enormous'], () => {
});

const VIEWPORTS = ['small', 'huge', 'enormous'];
Terra.describeViewports('Test', VIEWPORTS, () => {
});

const REFERENCE = VIEWPORTS;
Terra.describeViewports('Test', REFERENCE, () => {
});

function aFunction() {
}
const FUNCTION = aFunction();
Terra.describeViewports('Test', FUNCTION, () => {
});

Terra.describeViewports('Test', aFunction(), () => {
});

let NOT_INITIALIZED;
Terra.describeViewports('Test', NOT_INITIALIZED, () => {
});

let NON_VIEWPORTS_LET = 2;
Terra.describeViewports('Test', NON_VIEWPORTS_LET, () => {
});

// Highlight cases

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">'tiny'</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">{ }</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">false</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">8.4</error>, () => {
});

const NON_VIEWPORTS = 2;
Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">NON_VIEWPORTS</error>, () => {
});
