//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('outer describe', () => {
    describe(() => {
        Terra.it.matchesScreenshot('with name<caret>', { selector: '#selector' });
    });
});
