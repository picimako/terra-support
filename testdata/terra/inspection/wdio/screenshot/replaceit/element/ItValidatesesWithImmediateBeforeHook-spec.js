//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('', () => {
    before(() => {
        browser.url('/');
    });
    Terra.<caret>it.validatesElement('test', { selector: '#selector' });
    Terra.it.matchesScreenshot('test', { selector: '#anotherselector' });
});
