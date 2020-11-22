/* global Terra, browser, describe */
Terra.describeViewports('Test', ['tiny', 'small'], () => {
    <error descr="Nested Terra.describeViewports blocks are not allowed.">Terra.describeViewports</error>('Nested', ['huge', 'large'], () => {
    });

    describe('A describe block', () => {
        <error descr="Nested Terra.describeViewports blocks are not allowed.">Terra.describeViewports</error>('More nested', ['medium', 'enormous'], () => {
        });
        it('an it block', () => {
            browser.url('/some/url');
            Terra.validates.element();
        });
    });
});

Terra.describeViewports('Test', ['tiny', 'large'], () => {
    <error descr="Nested Terra.describeViewports blocks are not allowed.">Terra.describeViewports</error>('Nested', ['huge', 'large'], () => {
    });
});
