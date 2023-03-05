//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

//highlight cases

Terra.describeViewports('viewports', ['medium'], () => {
    describe('terra', () => {
        Terra.it.validatesElement('screenshot', { selector: <warning descr="This selector matches the global selector defined in wdio.conf.js. It may be removed from here.">".second-se'l'ector"</warning> });
    });
});

describe('describe', () => {
    describe('terra', () => {
        Terra.it.matchesScreenshot('screenshot', { selector: <warning descr="This selector matches the global selector defined in wdio.conf.js. It may be removed from here.">".second-se'l'ector"</warning> });
    });
});

//no-highlight cases

Terra.describeViewports('terra', ['medium'], () => {
    it('Test case', () => {
        Terra.validates.element('selector', { selector: '#first-selector' });
    });
});
