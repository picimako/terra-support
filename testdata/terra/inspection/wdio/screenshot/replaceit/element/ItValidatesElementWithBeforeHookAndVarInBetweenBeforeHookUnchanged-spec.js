//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('', () => {
    before(() => {
        browser.url('/');
        browser.url('/?param=value');
    });

    var someVariable;

    Terra.it.vali<caret>datesElement('test name', { selector: '#selector' });
});
