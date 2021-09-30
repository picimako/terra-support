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

Terra.describeViewports('Test', ['asd'], () => {
});

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['medium'], () => {
});

Terra.describeViewports('Test', ['medium', 'large', 'huge'], () => {
});

Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
});

Terra.describeViewports('Test', ['tiny', 'asd', 'medium', 'asd', 'huge', 'enormous'], () => {
});

Terra.describeViewports('Test', [large<error descr=", expected">'</error>, '<error descr=", expected">l</error>arge<error descr=", expected">'</error>, '<error descr=", expected">m</error>edium<error descr=", expected">'</error>], () => {<EOLError descr="Unclosed string literal"></EOLError>
}<error descr="statement expected">)</error>;

// Highlight cases

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['small', 'small'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['large', 'large', 'medium'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['large', 'medium', 'medium'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['huge', 'tiny', 'huge'], () => {
});

Terra.<warning descr="There are duplicate viewport values in this block.">describeViewports</warning>('Test', ['asd', 'enormous', 'asd', 'enormous', 'huge', 'enormous'], () => {
});
