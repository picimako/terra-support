//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra, describe */
Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
    describe('A describe block', () => {
        it('duplicate', () => {
            Terra.validates.element(<error descr="There is more than one assertion specified with this screenshot name. That is not allowed in terra-functional-testing.">'test case'</error>);
        });

        it('non-duplicate', () => {
            Terra.validates.screenshot('test');
        });

        it('duplicate', () => {
            Terra.validates.screenshot(<error descr="There is more than one assertion specified with this screenshot name. That is not allowed in terra-functional-testing.">'test case'</error>);
        });
    });

    Terra.it.validatesElement('test case', { misMatchTolerance: 0 });
});
