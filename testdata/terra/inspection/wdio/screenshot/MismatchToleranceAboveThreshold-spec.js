//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, describe */
Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
    describe('A describe block', () => {
        it('no-highlight cases for element', () => {
            Terra.validates.element();
            Terra.validates.element('test case');
            Terra.validates.element({ misMatchTolerance: 0.4 });
            Terra.validates.element({ misMatchTolerance: 0.5 });
            Terra.validates.element('test case', { misMatchTolerance: 0 });
            Terra.validates.element('test case', { misMatchTolerance: 0, axeRules: { } });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: "asdsad" });
            Terra.validates.element('test case', { misMatchTolerance: { } });
            Terra.validates.element('test case', { misMatchTolerance: false });
        });

        it('no-highlight cases for screenshot', () => {
            Terra.validates.screenshot();
            Terra.validates.screenshot('test case');
            Terra.validates.screenshot({ misMatchTolerance: 0.4 });
            Terra.validates.screenshot({ misMatchTolerance: 0.5 });
            Terra.validates.screenshot('test case', { misMatchTolerance: 0 });
            Terra.validates.screenshot('test case', { misMatchTolerance: 0, axeRules: { } });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: "asdsad" });
            Terra.validates.screenshot('test case', { misMatchTolerance: { } });
            Terra.validates.screenshot('test case', { misMatchTolerance: false });
        });

        it('highlight cases for element', () => {
            Terra.validates.element({ misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">0.6</warning> });
            Terra.validates.element('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">10</warning> });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning> });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning>, axeRules: { } });
        });

        it('highlight cases for screenshot', () => {
            Terra.validates.screenshot({ misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">0.6</warning> });
            Terra.validates.screenshot('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">10</warning> });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning> });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning>, axeRules: { } });
        });
    });

    //no-highlight cases for validatesElement
    Terra.it.validatesElement();
    Terra.it.validatesElement('test case');
    Terra.it.validatesElement({ misMatchTolerance: 0.4 });
    Terra.it.validatesElement({ misMatchTolerance: 0.5 });
    Terra.it.validatesElement('test case', { misMatchTolerance: 0 });
    Terra.it.validatesElement('test case', { misMatchTolerance: 0, axeRules: { } });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: "asdsad" });
    Terra.it.validatesElement('test case', { misMatchTolerance: { } });
    Terra.it.validatesElement('test case', { misMatchTolerance: false });

    //no-highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot();
    Terra.it.matchesScreenshot('test case');
    Terra.it.matchesScreenshot({ misMatchTolerance: 0.4 });
    Terra.it.matchesScreenshot({ misMatchTolerance: 0.5 });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: 0 });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: 0, axeRules: { } });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: "asdsad" });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: { } });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: false });

    //highlight cases for validatesElement
    Terra.it.validatesElement({ misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">0.6</warning> });
    Terra.it.validatesElement('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">10</warning> });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning> });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning>, axeRules: { } });

    //highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot({ misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">0.6</warning> });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">10</warning> });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning> });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">99.00</warning>, axeRules: { } });
});
