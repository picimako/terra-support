//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
