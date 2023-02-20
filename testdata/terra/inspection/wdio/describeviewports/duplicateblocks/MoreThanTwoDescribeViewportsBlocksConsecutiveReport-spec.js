//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

Terra.describeViewports('Name', ['tiny','small','huge'], () => {
    describe('matches screenshot', () => {
        it('matches screenshot', () => {
            Terra.validates.screenshot();
        });
    });
});

<warning descr="Multiple Terra.describeViewports blocks with the same set of viewports are specified in this file. They can be merged into a single describeViewports block.">Terra.describeViewports</warning>('Another name', ['tiny','huge'], () => {
    describe('matches something else', () => {
    });
});

<warning descr="Multiple Terra.describeViewports blocks with the same set of viewports are specified in this file. They can be merged into a single describeViewports block.">Terra.describeViewports</warning>('Another name', ['tiny','huge'], () => {
    describe('matches something else again', () => {
    });
});
