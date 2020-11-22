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
