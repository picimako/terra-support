//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

//highlight cases

Terra.describeViewports('viewports', ['medium'], () => {
    describe('terra', () => {
        Terra.it.validatesElement('screenshot', { selector: ".second-se'l'ector" });
    });
});

describe('describe', () => {
    describe('terra', () => {
        Terra.it.matchesScreenshot('screenshot', { selector: ".second-se'l'ector" });
    });
});

//no-highlight cases

Terra.describeViewports('terra', ['medium'], () => {
    it('Test case', () => {
        Terra.validates.element('selector', { selector: '#first-selector' });
    });
});
