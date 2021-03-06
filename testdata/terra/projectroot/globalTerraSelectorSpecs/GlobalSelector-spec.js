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

//highlight cases

Terra.describeViewports('viewports', ['medium'], () => {
    describe('terra', () => {
        Terra.it.validatesElement('screenshot', { selector: <warning descr="This selector matches the global selector defined in wdio.conf.js. It may be removed from here.">".second-se'l'ector"</warning> });
    });
});

describe('describe', () => {
    describe('terra', () => {
        Terra.it.matchesScreenshot('screenshot', { selector: <warning descr="This selector matches the global selector defined in wdio.conf.js. It may be removed from here.">".second-se'l'ector"</warning> });
    });
});

//no-highlight cases

Terra.describeViewports('terra', ['medium'], () => {
    it('Test case', () => {
        Terra.validates.element('selector', { selector: '#first-selector' });
    });
});
