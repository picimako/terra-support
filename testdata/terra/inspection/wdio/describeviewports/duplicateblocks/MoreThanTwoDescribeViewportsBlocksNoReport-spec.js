//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

Terra.describeViewports('Name', ['tiny','small','huge'], () => {
    describe('matches screenshot', () => {
        it('matches screenshot', () => {
            Terra.validates.screenshot();
        });
    });
});

Terra.describeViewports('Another name', ['tiny','huge'], () => {
    describe('mathes something else', () => {
    });
});

Terra.describeViewports('Another name', ['medium','tiny'], () => {
    describe('mathes something else again', () => {
    });
});
