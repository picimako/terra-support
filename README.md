# Terra Support JetBrains plugin

[![Terra Support](https://img.shields.io/jetbrains/plugin/v/15430-terra-support)](https://plugins.jetbrains.com/plugin/15430-terra-support)

This plugin provides engineers, who work with Cerner's various Terra libraries, different tools to speed up and make their work much easier.

## IDE features

The following sections give you a summary of what JetBrains IDE features are incorporated into this plugin throughout
different Terra feature areas.

### Code inspections

There are a handful of code inspections available for validating different aspects of Terra wdio tests.
These inspections are enabled by default, unless stated otherwise.

For the high-level types of inspections, see the [Contribution guide](/CONTRIBUTE.md#implementing-inspections).

### Quick Documentations

[Quick Documentation](https://www.jetbrains.com/help/idea/viewing-reference-information.html?keymap=primary_windows#inline-quick-documentation) is one of the documentation
types of JetBrains products which can be triggered via Ctrl+Q (on Windows) or F1 (on Mac). It shows a popup with some rendered information about the element in question.

### References

The feature called References provides a way to navigate from a reference of an element to the definition of it,
just like when you can jump to the definition of a function from the usage of that. 

### Tool windows

Tool windows are sidebar and bottom panels on the frame of the IDE window. There is [one Tool Window available](/docs/terra_wdio_tool_window.md) at the moment
for working with Terra screenshots.

### Inlay Hints

Inlay Hints are small labels injected into the code, that provide extra (often otherwise invisible) information about the code.
The [current Inlay Hints](docs/terra_helpers.md#inlay-hints) for Terra include displaying screenshot names and global Terra CSS selectors.

## Terra features and settings

You can navigate to further documentation categorized mostly by Terra features.

- [Terra Helpers](docs/terra_helpers.md)
- [Terra Viewports](docs/terra_viewports.md)
- [Terra UI and components](docs/terra_ui.md)
- [Terra Wdio Tool Window](docs/terra_wdio_tool_window.md)

There are also various customization options for the plugin which you can find on its dedicated [Plugin Settings page](docs/terra_settings.md).

## Plugin contribution guide

To add features, bugfixes or provide any kind of contribution to this project, please see the [Contribution guide](/CONTRIBUTE.md).

## Images and logos

The Terra Support plugin logo is a custom-drawn logo inspired by the original [Terra logo](https://engineering.cerner.com/terra-ui/home/terra-ui/index),
and it portrays a moon which orbits a planet, which orbits a "cold star". (Shout out to Doctor Who.)

## Licencing

This project and the [Terra Support plugin logo](src/main/resources/META-INF/pluginIcon.svg) is licensed under the terms of Apache Licence Version 2.0.

If you find anything copyrighted regarding Cerner or Terra material that must not be used in this project, or should be used with certain
restrictions or limitations, please let me know by creating an issue, then we can discuss the issue further in private channels,
after which I will look into it, and alter/modify/remove the material in question if necessary.
