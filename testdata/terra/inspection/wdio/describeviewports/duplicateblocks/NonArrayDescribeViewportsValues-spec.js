//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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

const NON_VIEWPORTS = 2;
Terra.describeViewports('Test', NON_VIEWPORTS, () => {
});
