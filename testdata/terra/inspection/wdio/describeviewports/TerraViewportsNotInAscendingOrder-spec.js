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

Terra.describeViewports('Test', ['tiny', 'medium', 'huge'], () => {
});

Terra.describeViewports('Test', ['tiny', 'small', 'medium', 'large', 'huge', 'enormous'], () => {
});

// Highlight cases

Terra.describeViewports('Test', <warning descr="Viewports are not in ascending order by their widths.">['small', 'tiny']</warning>, () => {
});

Terra.describeViewports('Test', <warning descr="Viewports are not in ascending order by their widths.">['large', 'huge', 'medium']</warning>, () => {
});

Terra.describeViewports('Test', <warning descr="Viewports are not in ascending order by their widths.">['medium', 'tiny', 'huge']</warning>, () => {
});

Terra.describeViewports('Test', <warning descr="Viewports are not in ascending order by their widths.">['tiny', 'enormous', 'medium', 'large', 'huge', 'small']</warning>, () => {
});
