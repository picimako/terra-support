//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra */

// No-highlight cases

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['small', 'huge', 'enormous'], () => {
});

// Highlight cases

Terra.describeViewports('Test', [<error descr="This viewport is not supported by Terra.">''</error>], () => {
});

Terra.describeViewports('Test', [<error descr="This viewport is not supported by Terra.">'gigantic'</error>], () => {
});

Terra.describeViewports('Test', ['small', <error descr="This viewport is not supported by Terra.">'medi'</error>, <error descr="This viewport is not supported by Terra.">'enorm'</error>], () => {
});
