//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

describe('outer describe', () => {
    describe('terra- screenshot--', () => {
        Terra.it.matchesScreenshot('with-_-replaced-_-characters_-', { selector: '#selector' });
    });

    describe('terra screenshot', () => {
        Terra.validates.element('collect');
    });

    describe('used', () => {
        Terra.it.matchesScreenshot({ selector: '#selector' });
        Terra.it.matchesScreenshot('this is the [partialid]', { selector: '#selector' });
    });

    describe('used', () => {
        Terra.it.matchesScreenshot('fromlatest', { selector: '#selector' });
        Terra.it.matchesScreenshot('fromdiff', { selector: '#selector' });
    });
});
