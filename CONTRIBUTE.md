# Contribution guide

If you would like to contribute, first make sure to check out the document about [how to setup the dev environment](/ENV_SETUP.md).

## Table of contents

- [Git branching](#git-branching)
- [How to add new File Templates](#how-to-add-new-file-templates)
- [Implementing inspections](#implementing-inspections)
    - [Blocking vs. non-blocking inspection types](#blocking-vs-non-blocking-inspection-types)
    - [Steps to implement an inspection](#steps-to-implement-an-inspection)
    - [Unit testing](#unit-testing)
- [Helper code snippets](#helper-code-snippets)

## Git branching

When you start working on a separate Git branch, please make sure the branch is named according to its purpose as follows:

    <branch_type>/#<issue_number>-<branch_name>

where branch_type may be `feature`, `bugfix` or `doc`, e.g. **feature/#23-add-new-inspection**.

Commit messages doesn't have to contain the issue numbers.

## How to add new File Templates

In order to add File Template under *Settings > Editor > File and Code Template > Files tab*, place your template file under `src/main/resources/fileTemplates`.

The file must be named as follows: `<template name>.<file extension of created file>.ft`, for example for a JavaScript file it could be `Jest test file.js.ft`.

## Implementing inspections

### Blocking vs. non-blocking inspection types

Inspections are structured and implemented in a way to have as less duplication and duplicated code execution as possible.

Since severity can only be assigned on a per inspection/implementation class basis (or extension point basis, depending on the viewpoint), inspection implementations are
separated into **blocking** and **non-blocking** types. (The naming is kind of arbitrary but reflects its purpose. Also, this separation is not a JetBrains standard but is introduced
here for better performance and maintenance.

**Blocking** inspections are registered with `ERROR` severity level in the `plugin.xml`, and each problem registered in the `ProblemsHolder` within the implementation
is considered a problem that would either cause the test execution not to start, or to fail because in insufficient value was provided that is not considered a valid input.

These classes have their names extended with the word `Blocking`, see e.g. `TerraDescribeViewportsBlockingInspection`.

**Non-blocking** inspections are registered with `WARNING` severity level (or even lower) in the `plugin.xml`, and each problem registered in the `ProblemsHolder` within the implementation
is considered a problem that would not cause the test execution not to start, or to fail, rather values or construct that would have unintended consequences of the test execution, or might
mislead users.

These classes don't have `Blocking` in their names, see e.g. `TerraDescribeViewportsInspection`.

### Steps to implement an inspection

- Create the implementation of the inspection. [[docs]](https://jetbrains.org/intellij/sdk/docs/tutorials/code_inspections.html)
    - As for determining the severity of the inspection, see the [Blocking vs. non-blocking inspection types](#blocking-vs-non-blocking-inspection-types) section in this document.
    - Consider customization of inspection configuration via the Settings UI.
- Register the implementation class in plugin.xml. [[docs]](https://jetbrains.org/intellij/sdk/docs/tutorials/code_inspections.html#plugin-configuration-file)
- Implement unit test(s) for the inspection with both no-highlight and highlight cases, and make sure they run successfully (and actually validate the logic behind). [[testing plugins]](https://jetbrains.org/intellij/sdk/docs/basics/testing_plugins/testing_plugins.html) [[testing highlighting]](https://jetbrains.org/intellij/sdk/docs/basics/testing_plugins/testing_highlighting.html) 
- The description HTML file is added for the inspection. They are located at [src/main/resources/inspectionDescriptions](src/main/resources/inspectionDescriptions).
- Check at least a few cases of the inspection manually, by running the IDE.
- The inspection is added to README.md with description, explanation, example code snippets for incorrect and correct (where feasible)
cases, with any necessary external documentations linked, and linking the implementation class.
    - In case of Replace templates, include code snippets about the before state, and the after state having the Quick Fix(es) applied.

### Unit testing

**Highlight cases** are positive test cases, meaning the cases in which code snippet(s) (Psi element(s)) need to be highlighted and have one or more problems registered to them.
**No-highlight cases** mean negative tests where no problem registration and code highlighting should happen.

In inspection unit test classes the followings must be configured:
- override `getTestDataPath()` to return the path where the test data for this particular test can be found
- override `getInspection()` to return the inspection implementation class this unit test validates
- implement test methods to validate each test data file, where the methods name must be `test<test data file name without extension>`, and call `do<extension>Test()` named according the file type validated.

Test data for inspection unit tests can be found under the `testdata` root directory.

Since test data files may contain additional XML elements for marking elements for expected highlighting (e.g. `<error descr=""></error>` and other severity level test tags),
including these elements will mark the file with an ERROR that it is invalid. You can ignore that, highlight testing will run regardless of that. 

## Troubleshooting

Since the wdio root folder value is cached in `TerraWdioFolders` it can cause unit test failures, so if you suspect that test cases might be affected by the wdio root set in other unit tests,
make sure to call `TerraWdioFolders.clearWdioRootCache()` before it/them.

## Helper code snippets

The following snippets might be helpful when generating/reorganizing data for `com.picimako.terra.documentation.TerraUIComponentDocumentationUrlService`.

**Reorder json objects alphabetically by componentName in terra-ui-component-docs.json**

```java
public String reorder() {
    final String template = "{\n" +
            "      \"componentName\": \"%s\",\n" +
            "      \"properties\": [\n" +
            "        {\n" +
            "          \"importPath\": \"%s\",\n" +
            "          \"relativeUrl\": \"%s\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n";

    return Arrays.stream(componentDocs.components)
            .sorted(Comparator.comparing(component -> component.componentName))
            .filter(item -> item.properties.length == 1)
            .map(item -> String.format(template, item.componentName, item.properties[0].importPath, item.properties[0].relativeUrl))
            .collect(Collectors.joining());
}
```
