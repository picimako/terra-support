//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, describe */
Terra.describeViewports('Test', ['large', 'huge'], () => {

    // No-highlight cases

    describe('A describe block', () => {
        it('an it block', () => {
            Terra.validates.element({ mismatchTolerance: 0.5 });
            Terra.validates.screenshot({ selector: '#id' });
            Terra.validates.accessibility({ rules: '' });
        });
    });

    //Highlight cases

    describe('A describe block', () => {
        it('an it block', () => {
            Terra.validates.element({ <error descr="This is not a valid property for this validation. The valid ones to use are: [selector, mismatchTolerance, rules]">selecta</error>: '#id' });
            Terra.validates.screenshot('name', { <error descr="This is not a valid property for this validation. The valid ones to use are: [mismatchTolerance, selector]">misMatchTolerance</error>: 0.5, <error descr="This is not a valid property for this validation. The valid ones to use are: [mismatchTolerance, selector]">axe</error>: '' });
            Terra.validates.accessibility({ <error descr="This is not a valid property for this validation. The valid ones to use are: [rules]">axeRules</error>: ''});
        });
    });
});
