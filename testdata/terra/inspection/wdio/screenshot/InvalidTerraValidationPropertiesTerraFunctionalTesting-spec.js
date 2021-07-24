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
Terra.describeViewports('Test', ['large', 'huge'], () => {

    // No-highlight cases

    describe('A describe block', () => {
        it('an it block', () => {
            Terra.validates.element({ mismatchTolerance: 0.5 });
            Terra.validates.screenshot({ selector: '#id' });
            Terra.validates.accessibility({ rules: '' });
        });
    });

    //Highlight cases

    describe('A describe block', () => {
        it('an it block', () => {
            Terra.validates.element({ <error descr="This is not a valid property for this validation. The valid ones to use are: [selector, mismatchTolerance, rules]">selecta</error>: '#id' });
            Terra.validates.screenshot('name', { <error descr="This is not a valid property for this validation. The valid ones to use are: [mismatchTolerance, selector]">misMatchTolerance</error>: 0.5, <error descr="This is not a valid property for this validation. The valid ones to use are: [mismatchTolerance, selector]">axe</error>: '' });
            Terra.validates.accessibility({ <error descr="This is not a valid property for this validation. The valid ones to use are: [rules]">axeRules</error>: ''});
        });
    });
});
