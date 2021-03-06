# Changelog

### 0.5.1

#### BUGFIX
- Fixed an issue with the diff and latest/reference previews due to API changes in IJ 2021.1. It caused the IDE
  to become unresponsive after opening either of these previews, due to a null value under the hood.

### 0.5.0

#### NEW
- [#38](https://github.com/picimako/terra-support/issues/38): A new **Navigate to Screenshot Usage** action is added to the Project View context menu for Terra screenshots,
so that navigation to code can happen from there as well, besides the tool window.
- [#27](https://github.com/picimako/terra-support/issues/27): Added a couple of settings options:
    - project statistics can now be toggled in the Terra wdio tool window by a dedicated toolbar action
    - screenshot line markers can now be enabled/disabled under `Settings > Editor > General > Gutter Icons`
    - wdio test root paths can be customized under `Settings > Tools > Terra Support`
    - an option to enabled/disable the confirmation dialog when deleting a screenshot via the tool window
- [#49](https://github.com/picimako/terra-support/issues/49):
  - Added Inlay Hints for Terra screenshot validation calls, to show the referenced screenshot names, and if a CSS selector
  is not explicitly specified, then show the global selector defined in `wdio.conf.js`.
  - This issue also introduces Kotlin support to the project, as the hints are implemented in Kotlin.

For more information on Inlay Hints, please visit:
- https://www.jetbrains.com/help/idea/viewing-reference-information.html#inlay-hints
- https://www.jetbrains.com/help/idea/inlay-hints.html

#### BUGFIX
- [#48](https://github.com/picimako/terra-support/issues/48): Fixed an issue in `TerraDescribeViewportsBlockingInspection` that reported false positive results when the viewports argument
in `Terra.describeViewports` blocks were referenced variables, function calls, etc. instead of a string literal. 

#### MAINTENANCE
- [#42](https://github.com/picimako/terra-support/issues/42): Optimized the project's unit test area, also making the project size much smaller.

### v0.4.1

#### ENHANCEMENT
- [#40](https://github.com/picimako/terra-support/issues/40): Extended the screenshot name resolution logic with test id handling based on [terra-toolkit/visualRegressionConf.js](https://github.com/cerner/terra-toolkit-boneyard/blob/main/config/wdio/visualRegressionConf.js)

#### BUGFIX
- [#41](https://github.com/picimako/terra-support/issues/41): Fixed the problem that the Find unused screenshot action in the tool window produced false positive results when
screenshots with the same name were present under multiple spec files.
- [#43](https://github.com/picimako/terra-support/issues/43): Fixed the problem that collecting screenshots happened by name and not by name and spec file. 
  This returned false positive results during reference and gutter icon creation, and the missing screenshot inspection.  

### v0.4.0

#### NEW
- [#30](https://github.com/picimako/terra-support/issues/30): Added partial support for TypeScript (.ts) spec files.
- [#31](https://github.com/picimako/terra-support/issues/31): Added inspection to report Terra.describeViewports blocks with the same set of viewport values within the same file. 
- [#26](https://github.com/picimako/terra-support/issues/26): Added gutter icons / line markers for screenshot validation calls in wdio spec files. Image references are added only
  to default-screenshot icons because non-default ones are referenced via the Terra calls' name parameters. 
- [#25](https://github.com/picimako/terra-support/issues/25): Added the **Navigate to Usage** action to the wdio tool window, so that users can jump to the code section
  where the screenshot's validation happens. 

### v0.3.1

#### BUGFIX
- [#28](https://github.com/picimako/terra-support/issues/28): Fixed the problem when double-clicking on a screenshot node in the Terra Wdio tool window
  that had no latest version of that screenshot present, it resulted in an `IndexOutOfBoundsException`.
- [#24](https://github.com/picimako/terra-support/issues/24): Fixed the problem that hitting Enter in the screenshot rename dialog also opened the editor
  for the selected screenshot. 

### v0.3.0

#### NEW
- [#15](https://github.com/picimako/terra-support/issues/15): Moved the majority of user-facing messages to a custom message bundle
- [#18](https://github.com/picimako/terra-support/issues/18): Added support for handling nested spec files and screenshots as part of
  - the Terra Wdio tool window
  - the missing screenshot inspection
  - screenshot references in Terra.it and Terra.validates calls
- [#14](https://github.com/picimako/terra-support/issues/14): Added an action to the Terra Wdio tool window to analyze the project and report screenshots
that are not referenced anywhere in tests

#### BUGFIX
- [#19](https://github.com/picimako/terra-support/issues/19): Fixed the problem where after deleting the last screenshot node in the Terra Wdio tool window,
the spec node still remained

### v0.2.0

#### NEW
- [#1](https://github.com/picimako/terra-support/issues/1): Added references between Terra.it/Terra.validates calls and screenshots
- [#2](https://github.com/picimako/terra-support/issues/2): Added inspection for missing screenshots in Terra.validates/Terra.it calls
- [#4](https://github.com/picimako/terra-support/issues/4): Added inspection to check whether the global Terra selector is used in Terra.validates/Terra.it calls

#### MAINTENANCE
- [#3](https://github.com/picimako/terra-support/issues/3): Reorganized documentation and "introduced in \<version>" sections to readme and javadoc

### v0.1.0

Initial release
