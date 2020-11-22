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
