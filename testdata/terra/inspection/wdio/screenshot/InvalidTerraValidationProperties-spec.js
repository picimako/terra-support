//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, describe */
Terra.describeViewports('Test', ['large', 'huge'], () => {

    // No-highlight cases

    describe('A describe block', () => {
        it('an it block', () => {
            Terra.validates.element({ misMatchTolerance: 0.5 });
            Terra.validates.screenshot({ selector: '#id' });
            Terra.validates.accessibility({ axeRules: '' });
        });
    });

    Terra.it.validatesElement();
    Terra.it.matchesScreenshot();
    Terra.it.isAccessible();

    //Highlight cases

    describe('A describe block', () => {
        it('an it block', () => {
            Terra.validates.element({ <error descr="This is not a valid property for this validation. The valid ones to use are: [selector, misMatchTolerance, axeRules]">selecta</error>: '#id' });
            Terra.validates.screenshot('name', { <error descr="This is not a valid property for this validation. The valid ones to use are: [misMatchTolerance, selector, viewports]">misMatchTol</error>: 0.5, <error descr="This is not a valid property for this validation. The valid ones to use are: [misMatchTolerance, selector, viewports]">axe</error>: '' });
            Terra.validates.accessibility({ <error descr="This is not a valid property for this validation. The valid ones to use are: [axeRules]">x</error>: ''});
        });
    });

    Terra.it.validatesElement({ <error descr="This is not a valid property for this validation. The valid ones to use are: [selector, misMatchTolerance, axeRules]">selecta</error>: '#id' });
    Terra.it.matchesScreenshot('name', { <error descr="This is not a valid property for this validation. The valid ones to use are: [misMatchTolerance, selector, viewports]">misMatchTol</error>: 0.5, <error descr="This is not a valid property for this validation. The valid ones to use are: [misMatchTolerance, selector, viewports]">axe</error>: '' });
    Terra.it.isAccessible({ <error descr="This is not a valid property for this validation. The valid ones to use are: [axeRules]">x</error>: ''});
});
