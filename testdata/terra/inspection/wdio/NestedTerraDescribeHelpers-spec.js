//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, browser, describe */
Terra.describeViewports('Viewports', ['tiny', 'small'], () => {
    <error descr="Nested Terra.describe helper blocks are not allowed.">Terra.describeViewports</error>('Nested viewports', ['huge', 'large'], () => {
    });

    describe('A describe block', () => {
        <error descr="Nested Terra.describe helper blocks are not allowed.">Terra.describeViewports</error>('More nested viewports', ['medium', 'enormous'], () => {
        });

        <error descr="Nested Terra.describe helper blocks are not allowed.">Terra.describeTests</error>('Nested tests', { formFactors: ['tiny', 'huge'] }, () => {
        });

        it('an it block', () => {
            browser.url('/some/url');
            Terra.validates.element();
        });
    });
});

Terra.describeViewports('Viewports', ['tiny', 'large'], () => {
    <error descr="Nested Terra.describe helper blocks are not allowed.">Terra.describeViewports</error>('Nested viewports', ['huge', 'large'], () => {
    });
});

Terra.describeTests('Tests', ['tiny', 'large'], () => {
    <error descr="Nested Terra.describe helper blocks are not allowed.">Terra.describeTests</error>('Nested tests', { formFactors: ['tiny', 'huge'] }, () => {
    });
});