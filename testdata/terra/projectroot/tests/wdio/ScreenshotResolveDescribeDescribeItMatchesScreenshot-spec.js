//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('outer describe', () => {
    describe('terra? screenshot<>', () => {
        Terra.it.matchesScreenshot('with/ |replaced*.:characters+"<caret>', { selector: '#selector' });
    });
});
