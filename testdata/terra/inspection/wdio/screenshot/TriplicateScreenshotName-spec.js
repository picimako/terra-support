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

/* global Terra, describe */
Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
    describe('A describe block', () => {
        it('triplicate', () => {
            Terra.validates.element(<error descr="There is more than one assertion specified with this screenshot name. That is not allowed in terra-functional-testing.">'test case'</error>);
        });

        it('non-triplicate', () => {
            Terra.validates.screenshot('test');
        });

        it('triplicate', () => {
            Terra.validates.screenshot(<error descr="There is more than one assertion specified with this screenshot name. That is not allowed in terra-functional-testing.">'test case'</error>);
        });

        it('non-triplicate', () => {
            Terra.validates.screenshot('test 2');
        });

        it('triplicate', () => {
            Terra.validates.screenshot('test case');
        });
    });

    Terra.it.validatesElement('test case', { misMatchTolerance: 0 });
});
