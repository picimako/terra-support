//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('', () => {
    before(() => {
        browser.url('/');
    });
    <caret>Terra.it.validatesElement('test name', { selector: '#selector' });
});
