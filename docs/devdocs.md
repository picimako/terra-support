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