//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

<warning descr="Multiple Terra.describeViewports blocks with the same set of viewports are specified in this file. They can be merged into a single describeViewports block.">Terra.describeViewports</warning>('Name', ['tiny','small','huge'], () => {
    describe('matches screenshot', () => {
        it('matches screenshot', () => {
            Terra.validates.screenshot();
        });
    });
});

<warning descr="Multiple Terra.describeViewports blocks with the same set of viewports are specified in this file. They can be merged into a single describeViewports block.">Terra.describeViewports</warning>('Another name', ['small','huge','tiny'], () => {
    describe('mathes something else', () => {
    });
});
