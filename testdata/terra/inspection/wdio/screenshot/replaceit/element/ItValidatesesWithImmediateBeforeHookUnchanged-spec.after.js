//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('', () => {
    before(() => {
        browser.url('/');
    });
    it('INSERT TEST NAME', () => {
        Terra.validates.element('test', {selector: '#selector'});
    });
    Terra.it.matchesScreenshot('test', { selector: '#anotherselector' });
});
