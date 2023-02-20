//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, describe */
Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
    describe('A describe block', () => {
        it('no-highlight cases for element', () => {
            Terra.validates.element('test case');
            Terra.validates.element('test case', { misMatchTolerance: 0 });
        });

        it('no-highlight cases for screenshot', () => {
            Terra.validates.screenshot('test case');
            Terra.validates.screenshot('test case', { misMatchTolerance: 0 });
        });

        it('highlight cases for element', () => {
            <error descr="The name argument must be specified.">Terra.validates.element</error>();
            <error descr="The name argument must be specified.">Terra.validates.element</error>({ misMatchTolerance: 10 });
        });

        it('highlight cases for screenshot', () => {
            <error descr="The name argument must be specified.">Terra.validates.screenshot</error>();
            <error descr="The name argument must be specified.">Terra.validates.screenshot</error>({ misMatchTolerance: 10 });
        });
    });

    //no-highlight cases for validatesElement
    Terra.it.validatesElement('test case');
    Terra.it.validatesElement('test case', { misMatchTolerance: 0 });

    //no-highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot('test case');
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: 0 });

    //highlight cases for validatesElement
    Terra.it.validatesElement();
    Terra.it.validatesElement({ misMatchTolerance: 10 });

    //highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot();
    Terra.it.matchesScreenshot({ misMatchTolerance: 10 });
});
