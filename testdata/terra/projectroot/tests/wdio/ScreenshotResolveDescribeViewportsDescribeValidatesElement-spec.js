//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

Terra.describeViewports('viewports', ['medium'], () => {
    describe('terra? screenshot<>', () => {
        it('Test case', () => {
            Terra.validates.element('with/ |re<caret>placed*.:characters+"', { selector: '#selector' });
        });
    });
});
