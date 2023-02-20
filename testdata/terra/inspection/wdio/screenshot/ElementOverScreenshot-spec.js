//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, browser, describe */
Terra.describeViewports('Test', ['large', 'huge'], () => {

    describe('A describe block', () => {
        it('an it block', () => {
            browser.url('/some/url');
            Terra.validates.<warning descr="Using element() is preferred over screenshot().">screenshot</warning>();
            Terra.validates.accessibility();
        });
    });

    Terra.it.validatesElement();

    describe('Another describe block', () => {
        it('another it block', () => {
            browser.url('/some/url');
            Terra.validates.element();
        });
    });

    Terra.it.<warning descr="Using validatesElement() is preferred over matchesScreenshot().">matchesScreenshot</warning>();

    Terra.it.isAccessible();
});
