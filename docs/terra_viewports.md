# Terra Viewports

## Inspections

### Incorrect viewport value(s) in Terra.describeViewports blocks

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TerraDescribeViewportsInspection-blue)](../src/main/java/com/picimako/terra/wdio/viewports/inspection/TerraDescribeViewportsInspection.java)

This inspection reports problems where one or more viewports defined in `Terra.describeViewports` and `Terra.describeTests` are not one of the breakpoints supported by Terra, namely:
tiny, small, medium, large, huge, enormous.

Be it a completely different viewport name, or just a typo, such values would cause the test/test suite execution not to start.

**Examples:**

```javascript
Terra.describeViewports('', ['tiny', 'small', 'med', 'huge', 'enorm'], () => {
});

Terra.describeViewports('', ['gigantic'], () => {
});

Terra.describeTests('', { formFactors: ['tiny', 'small', 'med', 'huge', 'enorm'] }, () => {
});

Terra.describeTests('', { formFactors: ['gigantic'] }, () => {
});
```

### Viewports in Terra.describeViewports should be enumerated in ascending order

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TerraDescribeViewportsInspection-blue)](../src/main/java/com/picimako/terra/wdio/viewports/inspection/TerraDescribeViewportsInspection.java)

This inspection reports problems where the viewports in `Terra.describeViewports` and `Terra.describeTests`are not specified in ascending order by their widths.

Although the order in what the viewports are specified doesn't affect the test execution negatively,
having them specified this way ensures that test implementations are consistent, and it is easily readable what viewports are actually covered.

**Examples:**

```javascript
Terra.describeViewports('', ['large', 'huge', 'medium'], () => {
    //correct: ['medium', 'large', 'huge']
});

Terra.describeViewports('', ['tiny', 'enormous', 'medium', 'large', 'huge', 'small'], () => {
    //correct: ['tiny', 'small', 'medium', 'large', 'huge', 'enormous']
});

Terra.describeTests('', { formFactors: ['large', 'huge', 'medium'] }, () => {
    //correct: ['medium', 'large', 'huge']
});

Terra.describeTests('', { formFactors:  ['tiny', 'enormous', 'medium', 'large', 'huge', 'small'] }, () => {
    //correct: ['tiny', 'small', 'medium', 'large', 'huge', 'enormous']
});
```

### Duplicate viewport values in Terra.describeViewports blocks

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TerraDescribeViewportsInspection-blue)](../src/main/java/com/picimako/terra/wdio/viewports/inspection/TerraDescribeViewportsInspection.java)

Copy-paste issues and typos can happen, so this inspection checks for duplicate viewport values, but only highlights
the ones that are actually supported by terra. The rest will be signaled by another inspection, that they are not supported.

**Example:**

```javascript
Terra.describeViewports('', ['small', 'small'], () => {
});

Terra.describeViewports('', ['asd', 'enormous', 'asd', 'enormous', 'huge', 'enormous'], () => {
});

Terra.describeTests('', { formFactors:  ['small', 'small'] }, () => {
});

Terra.describeTests('', { formFactors:  ['asd', 'enormous', 'asd', 'enormous', 'huge', 'enormous'] }, () => {
});
```

Having duplicate viewports is not an incorrect construct, and in itself it doesn't result in test execution failure.
To the contrary, it may be used to run test cases multiple times for the same viewport(s). But that is most probably not something you would want to
make a permanent part of the test implementation.

In the example above, the first `Terra.describeViewports` and the first `Terra.describeTests` blocks would execute the selected test cases for the *small* viewport twice.

### Blank viewports argument array in Terra.describeViewports blocks

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TerraDescribeViewportsInspection-blue)](../src/main/java/com/picimako/terra/wdio/viewports/inspection/TerraDescribeViewportsInspection.java)

This inspection reports problems where either the viewports argument is an empty array, or all items in it are blank
(either empty or contain only whitespaces).

**Example:**

```javascript
Terra.describeViewports('', [], () => {
});

Terra.describeViewports('', [''], () => {
});

Terra.describeViewports('', ['', '  ', ''], () => {
});

Terra.describeTests('', { formFactors:  [] }, () => {
});

Terra.describeTests('', { formFactors:  [''] }, () => {
});

Terra.describeTests('', { formFactors:  ['', '  ', ''] }, () => {
});
```

### Non-array-type viewports argument value in Terra.describeViewports blocks

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TerraDescribeViewportsInspection-blue)](../src/main/java/com/picimako/terra/wdio/viewports/inspection/TerraDescribeViewportsInspection.java)

The viewports argument of `Terra.describeViewports`, and the `formFactors` property of the `Terra.describeTests` testOptions argument accept an array only, so any other type of value will be reported by this check.

In v0.5.0 it is also extended to handle `const` variable references initialized as arrays. `var` and `let` type variables, as well as
function calls are ignored. This is so that the code doesn't need to add unnecessary complexity to try to find out the actual type of the variable
among the uncertain number of value assignments it may have.

**Example:**

```javascript
Terra.describeViewports('', { }, () => {
});

Terra.describeViewports('', 'tiny', () => {
});

Terra.describeViewports('', false, () => {
});

const VIEWPORTS = ['tiny','small'];
Terra.describeViewports('', VIEWPORTS, () => {
});

Terra.describeTests('', { formFactors: { } }, () => {
});

Terra.describeTests('', { formFactors: 'tiny' }, () => {
});

Terra.describeTests('', { formFactors:  false }, () => {
});
```

### Duplicate Terra.describeViewports blocks

![](https://img.shields.io/badge/since-0.4.0-blue) [![](https://img.shields.io/badge/implementation-DuplicateDescribeViewportsBlockInspection-blue)](../src/main/java/com/picimako/terra/wdio/viewports/inspection/DuplicateDescribeViewportsBlockInspection.java)

Since tests are organized into different blocks (`Terra.describeViewports`, `describe`, `it`, etc.) it may happen that within the same file there are multiple `Terra.describeViewports`
that are specified with the same set of viewport values. In this case it is probable that those blocks can be merged and all their test cases be handled under the same single
`describeViewports` block.

Neither the name parameter of the `describeViewports` block, nor the order of the viewport values are taken into account, so in the below example the first and the last blocks will be reported.

```javascript
Terra.describeViewports('Test', ['tiny', 'small'], () => { // This is reported.
});

Terra.describeViewports('Test', ['tiny','small','huge'], () => {
});

Terra.describeViewports('Another Test', ['small','tiny',], () => { // This is also reported.
});
```

## Quick Documentations

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TerraDescribeViewportsDocumentationProvider-blue)](../src/main/java/com/picimako/terra/documentation/TerraDescribeViewportsDocumentationProvider.java)

Each viewport String value (only the supported viewports) within `Terra.describeViewports`' and `Terra.describeTests`' argument list is extended with Quick Documentation, providing
basic information about the viewport at hand.

![terra-viewport-quick-documentation](assets/terra-viewport-quick-documentation.png)

The information shown is based on the Breakpoints table in the [Terra Breakpoints Guide](https://engineering.cerner.com/terra-ui/components/terra-breakpoints/breakpoints/about#breakpoints).
