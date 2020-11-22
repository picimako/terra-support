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

// Highlight cases

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">'tiny'</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">{ }</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">false</error>, () => {
});

Terra.describeViewports('Test', <error descr="Non-array-type values are not allowed for the viewports argument.">8.4</error>, () => {
});
