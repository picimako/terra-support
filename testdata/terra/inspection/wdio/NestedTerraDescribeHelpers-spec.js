/*
 * Copyright 2021 Tamás Balog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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