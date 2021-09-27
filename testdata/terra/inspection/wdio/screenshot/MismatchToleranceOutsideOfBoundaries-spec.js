/*
 * Copyright 2020 Tamás Balog
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
            Terra.validates.element();
            Terra.validates.element('test case');
            Terra.validates.element({ misMatchTolerance: 0.4 });
            Terra.validates.element({ misMatchTolerance: 0.5 });
            Terra.validates.element('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">50</warning> });
            Terra.validates.element('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">60</warning>, axeRules: { } });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: "asdsad" });
            Terra.validates.element('test case', { misMatchTolerance: { } });
            Terra.validates.element('test case', { misMatchTolerance: false });
        });

        it('no-highlight cases for screenshot', () => {
            Terra.validates.screenshot();
            Terra.validates.screenshot('test case');
            Terra.validates.screenshot({ misMatchTolerance: 0.4 });
            Terra.validates.screenshot({ misMatchTolerance: 0.5 });
            Terra.validates.screenshot('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">50</warning> });
            Terra.validates.screenshot('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">60</warning>, axeRules: { } });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: "asdsad" });
            Terra.validates.screenshot('test case', { misMatchTolerance: { } });
            Terra.validates.screenshot('test case', { misMatchTolerance: false });
        });

        it('highlight cases for element', () => {
            Terra.validates.element({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
            Terra.validates.element({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
            Terra.validates.element('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
            Terra.validates.element('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error>, axeRules: { } });
            Terra.validates.element('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error>, axeRules: { } });
        });

        it('highlight cases for screenshot', () => {
            Terra.validates.screenshot({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
            Terra.validates.screenshot({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
            Terra.validates.screenshot('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
            Terra.validates.screenshot('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error>, axeRules: { } });
            Terra.validates.screenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error>, axeRules: { } });
        });
    });

    //no-highlight cases for validatesElement
    Terra.it.validatesElement();
    Terra.it.validatesElement('test case');
    Terra.it.validatesElement({ misMatchTolerance: 0.4 });
    Terra.it.validatesElement({ misMatchTolerance: 0.5 });
    Terra.it.validatesElement('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">50</warning> });
    Terra.it.validatesElement('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">60</warning>, axeRules: { } });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: "asdsad" });
    Terra.it.validatesElement('test case', { misMatchTolerance: { } });
    Terra.it.validatesElement('test case', { misMatchTolerance: false });

    //no-highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot();
    Terra.it.matchesScreenshot('test case');
    Terra.it.matchesScreenshot({ misMatchTolerance: 0.4 });
    Terra.it.matchesScreenshot({ misMatchTolerance: 0.5 });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">50</warning> });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <warning descr="The mismatch tolerance is above the max threshold (0.5)">60</warning>, axeRules: { } });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: "asdsad" });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: { } });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: false });

    //highlight cases for validatesElement
    Terra.it.validatesElement({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
    Terra.it.validatesElement({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
    Terra.it.validatesElement('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
    Terra.it.validatesElement('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error> });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-1</error>, axeRules: { } });
    Terra.it.validatesElement('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error>, axeRules: { } });

    //highlight cases for matchesScreenshot
    Terra.it.matchesScreenshot({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-20</error> });
    Terra.it.matchesScreenshot({ misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-20</error> });
    Terra.it.matchesScreenshot('test case', { misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-10</error> });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error> });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">-10</error>, axeRules: { } });
    Terra.it.matchesScreenshot('test case', { selector: '#page', misMatchTolerance: <error descr="The mismatch tolerance should be a number between 0 and 100.">110</error>, axeRules: { } });
});
