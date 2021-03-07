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

Terra.describeViewports('Name', ['tiny','small','huge'], () => {
    describe('matches screenshot', () => {
        it('matches screenshot', () => {
            Terra.validates.screenshot();
        });
    });
});

<warning descr="Multiple Terra.describeViewports blocks with the same set of viewports are specified in this file. They can be merged into a single describeViewports block.">Terra.describeViewports</warning>('Another name', ['tiny','huge'], () => {
    describe('mathes something else', () => {
    });
});

<warning descr="Multiple Terra.describeViewports blocks with the same set of viewports are specified in this file. They can be merged into a single describeViewports block.">Terra.describeViewports</warning>('Another name', ['tiny','huge'], () => {
    describe('mathes something else again', () => {
    });
});
