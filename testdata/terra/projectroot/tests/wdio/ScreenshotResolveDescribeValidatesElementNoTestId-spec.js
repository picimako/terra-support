//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('terra screenshot', () => {
    it('Test case', () => {
        Terra.validates.element('i <caret>am [not ) single]', { selector: '#selector' });
    });
});
