# Dev docs

## JDK setup

In order to be able to work with the project, make sure to install Java 11 SDK, either manually or via IntelliJ's built-in
installation feature. Also configure necessary environment variables like JAVA_HOME.

### Gradle

This project is built on Gradle. Make sure you have the necessary environment variables set on your machine.

## Unit and integration testing

The following base test classes exist:
- `com.intellij.testFramework.fixtures.BasePlatformTestCase` for tests that doesn't need to be specific to
either terra-toolkit or terra-functional-testing
- `com.picimako.terra.TerraToolkitTestCase` for terra-toolkit specific tests
- `com.picimako.terra.TerraFunctionalTestingTestCase` for terra-functional-testing specific tests

- `com.picimako.terra.wdio.TerraInspectionBaseTestCase`: main base class for inspection testing
- `com.picimako.terra.wdio.TerraToolkitInspectionTestCase` for terra-toolkit specific inspection tests
- `com.picimako.terra.wdio.TerraFunctionalTestingInspectionTestCase` for terra-functional-testing specific inspection tests

Some classes use Mockito for mocking certain functionality, while many more use an integration test based approach.

Test data is available in the `testdata` folder in the project root.

### Running the plugin in WebStorm/IntelliJ Ultimate

This plugin is developed for IntelliJ Ultimate but with a little extra configuration it can also be ran in WebStorm.

For that, uncomment the following section in the build.gradle file:

```groovy
runIde {
    //https://jetbrains.org/intellij/sdk/docs/products/dev_alternate_products.html#configuring-pluginxml
    ideDirectory '<absolute path to WebStorm/IntelliJ Ultimate installation>'
}
```

and configure the absolute path of your WebStorm installation. For further details you can refer to the linked documentation.

### Install PsiViewer plugin

The [PsiViewer plugin](https://plugins.jetbrains.com/plugin/227-psiviewer) gives insight to the PSI structure of files,
including a tree view of the whole PSI tree, and to the properties of each node in the tree.

### IntelliJ Program Structure Interface

To get at least a basic understanding of what PSI is and how it works, you can head over to the
[JetBrains official documentation](https://jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)
of the topic.

## Plugin description

`plugindescription.html` contains the brief description of this plugin that is displayed both on JetBrains Marketplace
and in the Plugin manager within the IDE.

## Changelogs

Changelogs are available at `changelog.html` whose contents are displayed on JetBrains Marketplace and in the Plugin manager.

`CHANGELOG.md` collects changes to be available on GitHub only.

## Misc.

### Helper code snippets

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