//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

Terra.describeViewports('  terra? screenshot<>  ', ['medium'], () => {
    it('Test case', () => {
        Terra.validates.screenshot(<caret>'  with/ |replaced*.:characters+"  ', { selector: '#selector' });
    });
});
