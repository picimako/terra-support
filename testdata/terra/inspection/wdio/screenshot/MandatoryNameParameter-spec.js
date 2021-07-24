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
        it('no-highlight cases for element', () => {
            Terra.validates.element('test case');
            Terra.validates.element('test case', { misMatchTolerance: 0 });
        });

        it('no-highlight cases for screenshot', () => {
            Terra.validates.screenshot('test case');
            Terra.validates.screenshot('test case', { misMatchTolerance: 0 });
        });

        it('highlight cases for element', () => {
            <error descr="The name argument must be specified.">Terra.validates.element</error>();
            <error descr="The name argument must be specified.">Terra.validates.element</error>({ misMatchTolerance: 10 });
        });

        it('highlight cases for screenshot', () => {
            <error descr="The name argument must be specified.">Terra.validates.screenshot</error>();
            <error descr="The name argument must be specified.">Terra.validates.screenshot</error>({ misMatchTolerance: 10 });
        });
    });

    //no-highlight cases for validatesElement
    Terra.it.validatesElement('test case');
    Terra.it.validatesElement('test case', { misMatchTolerance: 0 });

    //no-highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot('test case');
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: 0 });

    //highlight cases for validatesElement
    Terra.it.validatesElement();
    Terra.it.validatesElement({ misMatchTolerance: 10 });

    //highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot();
    Terra.it.matchesScreenshot({ misMatchTolerance: 10 });
});
