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

/* global Terra */

// No-highlight cases

Terra.describeViewports('Test', [], () => {
});

Terra.describeViewports('Test', [''], () => {
});

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['small', 'huge', 'enormous'], () => {
});

const VIEWPORTS = ['small', 'huge', 'enormous'];
Terra.describeViewports('Test', VIEWPORTS, () => {
});

const REFERENCE = VIEWPORTS;
Terra.describeViewports('Test', REFERENCE, () => {
});

function aFunction() {
}
const FUNCTION = aFunction();
Terra.describeViewports('Test', FUNCTION, () => {
});

Terra.describeViewports('Test', aFunction(), () => {
});

let NOT_INITIALIZED;
Terra.describeViewports('Test', NOT_INITIALIZED, () => {
});

let NON_VIEWPORTS_LET = 2;
Terra.describeViewports('Test', NON_VIEWPORTS_LET, () => {
});

// Highlight cases

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">'tiny'</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">{ }</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">false</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">8.4</error>, () => {
});

const NON_VIEWPORTS = 2;
Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">NON_VIEWPORTS</error>, () => {
});
