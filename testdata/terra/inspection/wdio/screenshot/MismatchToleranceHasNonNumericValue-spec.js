//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
        });

        it('no-highlight cases for screenshot', () => {
            Terra.validates.screenshot();
            Terra.validates.screenshot('test case');
            Terra.validates.screenshot({ misMatchTolerance: 0.4 });
            Terra.validates.screenshot({ misMatchTolerance: 0.5 });
            Terra.validates.screenshot('test case', { misMatchTolerance: 0 });
            Terra.validates.screenshot('test case', { misMatchTolerance: 0, axeRules: { } });
        });

        it('highlight cases for element', () => {
            Terra.validates.element({ misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
            Terra.validates.element('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">{ }</error> });
            Terra.validates.element('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">false</error> });
        });

        it('highlight cases for screenshot', () => {
            Terra.validates.screenshot({ misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
            Terra.validates.screenshot('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">{ }</error> });
            Terra.validates.screenshot('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">false</error> });
        });
    });

    //no-highlight cases for validatesElement
    Terra.it.validatesElement();
    Terra.it.validatesElement('test case');
    Terra.it.validatesElement({ misMatchTolerance: 0.4 });
    Terra.it.validatesElement({ misMatchTolerance: 0.5 });
    Terra.it.validatesElement('test case', { misMatchTolerance: 0 });
    Terra.it.validatesElement('test case', { misMatchTolerance: 0, axeRules: { } });

    //no-highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot();
    Terra.it.matchesScreenshot('test case');
    Terra.it.matchesScreenshot({ misMatchTolerance: 0.4 });
    Terra.it.matchesScreenshot({ misMatchTolerance: 0.5 });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: 0 });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: 0, axeRules: { } });

    //highlight cases for validatesElement
    Terra.it.validatesElement({ selector: '#page', misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
    Terra.it.validatesElement('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">{ }</error> });
    Terra.it.validatesElement('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">false</error> });

    //highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot({ selector: '#page', misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">"asdsad"</error> });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">{ }</error> });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <error descr="The misMatchTolerance property accepts only numeric value between 0 and 100.">false</error> });
});
