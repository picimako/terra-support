# Terra Support Plugin for JetBrains IDEs

[![Terra Support](https://img.shields.io/jetbrains/plugin/v/15430-terra-support)](https://plugins.jetbrains.com/plugin/15430-terra-support)
![](https://img.shields.io/badge/since-v2021.3-blue)

<!-- Plugin description -->
This plugin provides integration to Cerner's various Terra libraries, especially with their test automation elements.

The automation specific part of the plugin provides tools for easier handling of screenshots, and for reporting
coding issues, including but not limited to:
- Inspections for viewports and screenshots.
- Custom tool window for a better overview of Terra wdio screenshots, extended with bulk operations on them.
- Custom editors for previewing diff images, and comparing reference and latest screenshots.
- Additional navigation options and visual clues for screenshot validation.
- Added and extended Quick Documentation popup contents.

The part that focuses on the [Terra UI components](https://engineering.cerner.com/terra-ui/home/terra-ui/index") adds
external documentation URLs to UI component React tags for easier navigation to related documentation.
<!-- Plugin description end -->

## Features and settings

The followings are a summary of the features incorporated into this plugin:

- **Code inspections**: to validate different aspects of Terra wdio tests. They are enabled by default, unless stated otherwise.
- **Quick Documentations**: [Quick Documentations](https://www.jetbrains.com/help/idea/viewing-reference-information.html?keymap=primary_windows#inline-quick-documentation)
  can be triggered via Ctrl+Q (on Windows) or F1 (on Mac). It shows a popup with some rendered information about the element in question.
- **References**: The feature called References provides a way to navigate from a reference of an element to the definition of it,
  just like when you jump to the definition of a function from the usage of that.
- **Tool Window**: There is a [Tool Window called Terra Wdio](/docs/terra_wdio_tool_window.md) available to work with Terra screenshots.
- **Inlay Hints**: These are small labels injected into the code, that provide extra (often otherwise invisible) information about the code.
  The [current Inlay Hints](docs/terra_helpers.md#inlay-hints) include displaying screenshot names and global Terra CSS selectors.

The documents below provide more details about the various features:

- [Terra Helpers](docs/terra_helpers.md)
- [Terra Viewports](docs/terra_viewports.md)
- [Terra UI and components](docs/terra_ui.md)
- [Terra Wdio Tool Window](docs/terra_wdio_tool_window.md)

There are also various customization options for the plugin which you can find on its dedicated [Plugin Settings page](docs/terra_settings.md).

## Developer documentation

To get started with the development of this project you can find some details in the [dev docs](docs/devdocs.md).

## Cerner Terra resources

- Terra Toolkit: [GitHub repository](https://github.com/cerner/terra-toolkit-boneyard), [Documentation](https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md)
- Terra Functional Testing: [GitHub repository](https://github.com/cerner/terra-toolkit/tree/main/packages/terra-functional-testing), [Changelog](https://github.com/cerner/terra-toolkit/blob/main/packages/terra-functional-testing/CHANGELOG.md),
[Documentation](https://engineering.cerner.com/terra-ui/dev_tools/cerner-terra-toolkit-docs/terra-functional-testing/about)

## Images and logos

The Terra Support plugin logo is a custom-drawn logo inspired by the original [Terra logo](https://engineering.cerner.com/terra-ui/home/terra-ui/index),
and it portrays a moon which orbits a planet, which orbits a "cold star". (Shout out to Doctor Who.)

## Licencing

This project and the [Terra Support plugin logo](src/main/resources/META-INF/pluginIcon.svg) is licensed under the terms of Apache Licence Version 2.0.
