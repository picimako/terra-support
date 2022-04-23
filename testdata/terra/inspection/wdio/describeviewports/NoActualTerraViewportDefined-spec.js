//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

/* global Terra */

// No-highlight cases

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['small', 'huge', 'enormous'], () => {
});

Terra.describeViewports('Test', ['small', '', 'enormous'], () => {
});

// Highlight cases

Terra.describeViewports('Test', <error descr="There is no actual viewport specified.">[]</error>, () => {
});

Terra.describeViewports('Test', <error descr="There is no actual viewport specified.">['']</error>, () => {
});

Terra.describeViewports('Test', <error descr="There is no actual viewport specified.">['', '']</error>, () => {
});
