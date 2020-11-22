### Java version

This plugin is built on Java 11, so make sure you have JDK 11 installed and configured on your machine, including necessary environment variables.

### Gradle

This project is based on Gradle, its version 6.6.1 as of the latest version of this document. Make sure you have the necessary environment variables set on your machine.

### Clone intellij-community project

Clone the [intelli-community](https://github.com/JetBrains/intellij-community) GitHub project to your machine,
then uncomment the following section in build.gradle:

```groovy
test {
    systemProperty 'idea.home.path', '<absolute path to locally cloned intellij-community GitHub repository>'
}
```

and set it to the absolute path of the repository you have just cloned.

This is required for loading some necessary libs for unit testing inspections.

### Running the plugin in WebStorm/IntelliJ Ultimate

This plugin is developed for IntelliJ Ultimate but with a little extra configuration it can also be ran in WebStorm.

For that uncomment the following section in the build.gradle file:

```groovy
runIde {
    ideDirectory '<absolute path to WebStorm/IntelliJ Ultimate installation>' //https://jetbrains.org/intellij/sdk/docs/products/dev_alternate_products.html#configuring-pluginxml
}
```

and configure the absolute path of your WebStorm installation. For further details you can refer to the linked documentation.

### Install PsiViewer plugin

The [PsiViewer plugin](https://plugins.jetbrains.com/plugin/227-psiviewer) gives insight to the PSI structure of files,
including a tree view of the whole PSI tree, and to the properties of each node in the tree. 

### IntelliJ Program Structure Interface

To get at least a basic understanding of what PSI is and how it works, you can head over to the [JetBrains official documentation](https://jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)
of the topic.

### IntelliJ import order

Make sure you have the following settings applied regarding wildcard imports and import order under *Settings > Editor > Code Style > Java > Imports*:

```
import static all other imports
<blank line>
import java.*
import javax.*
<blank line>
import all other imports
<blank line>
import com.picimako.*
```
