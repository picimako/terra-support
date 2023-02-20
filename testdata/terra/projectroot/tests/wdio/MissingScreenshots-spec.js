//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

//no-highlight cases

Terra.describeViewports('viewports', ['medium'], () => {
    describe('terra screenshot', () => {
        Terra.it.validatesElement('nondefault', { selector: '#selector' });
    });
});

describe('outer describe', () => {
    describe('terra screenshot', () => {
        Terra.it.matchesScreenshot('single');
    });
});

describe('outer describe', () => {
    describe('testimage', () => {
        Terra.it.matchesScreenshot('this is [default]');
    });
});

//highlight cases

Terra.describeViewports('terra', ['medium'], () => {
    it('Test case', () => {
        Terra.validates.element(<error descr="No reference screenshot exists for any context, for the name specified.">'some other name'</error>, { selector: '#selector' });
    });
});

describe('another describe', () => {
    describe('terra screenshot', () => {
        Terra.it.matchesScreenshot(<error descr="No reference screenshot exists for any context, for the name specified.">'this is [def)ault]'</error>);
    });
});
