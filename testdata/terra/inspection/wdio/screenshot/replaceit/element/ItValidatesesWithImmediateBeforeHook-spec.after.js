//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('', () => {
    
    it('INSERT TEST NAME', () => {
        browser.url('/');
        Terra.validates.element('test', {selector: '#selector'});
    });
    Terra.it.matchesScreenshot('test', { selector: '#anotherselector' });
});
