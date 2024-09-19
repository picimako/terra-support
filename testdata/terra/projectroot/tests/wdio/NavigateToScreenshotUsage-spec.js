//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

Terra.describeViewports('terra screenshot', ['medium'], () => {
    it('Test case', () => {
        browser.waitForVisible('#selector');
        Terra.validates.screenshot({ selector: '#selector' });
    });

    it('Test case', () => {
        Terra.validates.element('non-default', { selector: '#selector' });
    });
});
