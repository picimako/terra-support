/* global Terra */

// No-highlight cases

Terra.describeViewports('Test', ['tiny'], () => {
});

Terra.describeViewports('Test', ['small', 'huge', 'enormous'], () => {
});

// Highlight cases

Terra.describeViewports('Test', [<error descr="This viewport is not supported by Terra.">'gigantic'</error>], () => {
});

Terra.describeViewports('Test', ['small', <error descr="This viewport is not supported by Terra.">'medi'</error>, <error descr="This viewport is not supported by Terra.">'enorm'</error>], () => {
});
