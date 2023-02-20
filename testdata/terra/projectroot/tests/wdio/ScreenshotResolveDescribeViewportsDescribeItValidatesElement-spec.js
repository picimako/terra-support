//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

Terra.describeViewports('viewports', ['medium'], () => {
    describe.skip('terra? screenshot<>', () => {
        Terra.it.validatesElement(<caret>'with/ |replaced*.:characters+"', { selector: '#selector' });
    });
});
