# Changelog

## [Unreleased]

## [1.9.0]
### Changed
- New supported IDE version range: 2024.2 - 2024.3.*.
- Updated the project to use the IntelliJ Platform Gradle Plugin 2.0.
- Updated the project to use JDK 21.

## [1.8.0]
### Changed
- New supported IDE version range: 2023.1.6-2024.2-EAP.
- Plugin configuration and dependency updates.

## [1.7.0]
### Changed
- Temporarily disable quick documentations for Terra Wdio functions in Terra spec files.

## [1.6.0]
### Changed
- New supported IDE version range: 2022.3.3-2024.1-EAP.
- Plugin dependency updates.

## [1.5.0]
### Changed
- New supported IDE version range: 2022.2-2023.3
- Plugin configuration and dependency updates.

### Fixed
- [#62](https://github.com/picimako/terra-support/issues/62): Fixed a `ClassCastException` when retrieving the wdio tree component.

## [1.4.0]

### Added
- Offer this plugin when a project has dependency on the `@cerner/terra-functional-testing` npm package.

### Changed
- Spec and screenshot nodes are now marked with a dedicated diff icon, instead of bold text, when they have a diff image. This was necessary because the font setting is not being applied by the IDE.
- Updated a few URL for the quick documentation popups.
- Plugin configuration updates.

## [1.3.1]

### Changed
- Removed support for 2021.3, and added support for 2023.2

## [1.3.0]

### Changed
- Removed support for version 2021.2 of IDEs.
- Added support for 2023.1.

### Fixed
- Improved the retrieval of the global terra selector, in turn stabilizing the CSS selector inlay hint in test files.
- Fixed a memory leak regarding disposal of diff and reference/latest previews.

## [1.2.0]

### Changed
- Removed support for version 2021.1 of IDEs.
- NOTE: Due to a kotlin related issue, the screenshot and CSS selector inlay hints are not displayed in WS-2022.3, for now.
- Various code and documentation related housekeeping
- Migrated class instantiation to static instances and project services to minimize object instance creation.

### Fixed
- It fixes an NPE during calculating the position of the block inlay hints in the hint preview in Settings. 

## [1.1.0]

### Changed
- **IMPORTANT**: to minimize maintenance cost this plugin no longer supports IDE versions 2020.2 and 2020.3.
- Added support for release 2022.1 EAP.

## [1.0.0]

### Fixed
- [#57](https://github.com/picimako/terra-support/issues/57): Fixed an issue when rolling back Git changes on screenshots causes incorrect VirtualFiles to be stored.

### Changed
- Upgraded library and plugin versions.
- Fine-tuned various segments of the documentation.
- Did some minor code simplifications.

## [0.7.0]

### Changed
- Cleanup and simplification in several inspections.
- Merged `TerraDescribeViewportsBlockingInspection` into `TerraDescribeViewportsInspection`,
and `ScreenshotMismatchToleranceBlockingInspection` into `ScreenshotMismatchToleranceInspection`.
- Refined documentation.
- Upgraded gradle and intellij-gradle-plugin versions.
- Screenshot reference suggestions are now sorted alphabetically when Ctrl+Clicking on screenshot names in `Terra.validates`
and `Terra.it`
- [#56](https://github.com/picimako/terra-support/issues/56): Added `Terra.describeTests` besides `Terra.describeViewports` to be validated when they are nested in other describe blocks.
- Added viewport value validation to the testOptions argument of `Terra.describeTests` when it is specified in an inline object.
- Added viewport quick documentation support for the testOptions argument of `Terra.describeTests` when it is specified in an inline object.

### Fixed
- Fixed a bug in default name resolution that spammed the logs with exception.
- Fixed a ClassCastException log spam during viewport value retrieval.

## [0.6.0]

### Added

**Added support for Terra Functional-Testing**

[#53](https://github.com/picimako/terra-support/issues/53): Added support for the new terra-functional-testing npm package that brings
a couple of changes to how Terra wdio testing is conducted.

Whether terra-functional-testing or terra-toolkit is used is determined by whether `@cerner/terra-functional-testing` or `terra-toolkit`
is included in the dependencies of the project's root package.json. If none of them is found, plugin features will not execute.

Areas of changes:
- Updated the screenshot name resolution logic to use only the name argument of `Terra.validates` calls. This affected references, inlay hints,
  navigation to screenshot usage actions and inspections. See [Terra Upgrade Guide/Screenshots](https://engineering.cerner.com/terra-ui/dev_tools/cerner/terra-functional-testing/upgrade-guides/version-1-upgrade-guide#screenshots).
- Updated screenshot context string resolution to include the theme in the context string. This affects the reference suggestions and the Diff and Reference/Latest previews.
- Unused screenshot seeking logic is updated as well.
- Screenshot gutter icons no longer suggest screenshots when the name argument of a `Terra.validates` call is missing,
  since it is not a valid usage in terra-functional-testing.
- Inspections validating mismatch tolerance are updated to handle the renamed `mismatchTolerance` property along with `misMatchTolerance`.
- Various actions are updated to handle the new theme-specific folder structure.
- Added an inspection to report when the name parameter of a screenshot validation is missing.
- Added an inspection to report when more than one screenshot validation has the same name parameter, since these names must be unique.

### Fixed
- Fixed the disposal logic of the Terra wdio tree nodes.

## [0.5.1]

### Fixed
- Fixed an issue with the diff and latest/reference previews due to API changes in IJ 2021.1. It caused the IDE
  to become unresponsive after opening either of these previews, due to a null value under the hood.

## [0.5.0]

### Added
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

### Fixed
- [#48](https://github.com/picimako/terra-support/issues/48): Fixed an issue in `TerraDescribeViewportsBlockingInspection` that reported false positive results when the viewports argument
in `Terra.describeViewports` blocks were referenced variables, function calls, etc. instead of a string literal. 

### Changed
- [#42](https://github.com/picimako/terra-support/issues/42): Optimized the project's unit test area, also making the project size much smaller.

## [0.4.1]

### Changed
- [#40](https://github.com/picimako/terra-support/issues/40): Extended the screenshot name resolution logic with test id handling based on [terra-toolkit/visualRegressionConf.js](https://github.com/cerner/terra-toolkit-boneyard/blob/main/config/wdio/visualRegressionConf.js)

### Fixed
- [#41](https://github.com/picimako/terra-support/issues/41): Fixed the problem that the Find unused screenshot action in the tool window produced false positive results when
screenshots with the same name were present under multiple spec files.
- [#43](https://github.com/picimako/terra-support/issues/43): Fixed the problem that collecting screenshots happened by name and not by name and spec file. 
  This returned false positive results during reference and gutter icon creation, and the missing screenshot inspection.  

## [0.4.0]

### Added
- [#30](https://github.com/picimako/terra-support/issues/30): Added partial support for TypeScript (.ts) spec files.
- [#31](https://github.com/picimako/terra-support/issues/31): Added inspection to report Terra.describeViewports blocks with the same set of viewport values within the same file. 
- [#26](https://github.com/picimako/terra-support/issues/26): Added gutter icons / line markers for screenshot validation calls in wdio spec files. Image references are added only
  to default-screenshot icons because non-default ones are referenced via the Terra calls' name parameters. 
- [#25](https://github.com/picimako/terra-support/issues/25): Added the **Navigate to Usage** action to the wdio tool window, so that users can jump to the code section
  where the screenshot's validation happens. 

## [0.3.1]

### Fixed
- [#28](https://github.com/picimako/terra-support/issues/28): Fixed the problem when double-clicking on a screenshot node in the Terra Wdio tool window
  that had no latest version of that screenshot present, it resulted in an `IndexOutOfBoundsException`.
- [#24](https://github.com/picimako/terra-support/issues/24): Fixed the problem that hitting Enter in the screenshot rename dialog also opened the editor
  for the selected screenshot. 

## [0.3.0]

### Added
- [#15](https://github.com/picimako/terra-support/issues/15): Moved the majority of user-facing messages to a custom message bundle
- [#18](https://github.com/picimako/terra-support/issues/18): Added support for handling nested spec files and screenshots as part of
  - the Terra Wdio tool window
  - the missing screenshot inspection
  - screenshot references in Terra.it and Terra.validates calls
- [#14](https://github.com/picimako/terra-support/issues/14): Added an action to the Terra Wdio tool window to analyze the project and report screenshots
that are not referenced anywhere in tests

### Fixed
- [#19](https://github.com/picimako/terra-support/issues/19): Fixed the problem where after deleting the last screenshot node in the Terra Wdio tool window,
the spec node still remained

## [0.2.0]

### Added
- [#1](https://github.com/picimako/terra-support/issues/1): Added references between Terra.it/Terra.validates calls and screenshots
- [#2](https://github.com/picimako/terra-support/issues/2): Added inspection for missing screenshots in Terra.validates/Terra.it calls
- [#4](https://github.com/picimako/terra-support/issues/4): Added inspection to check whether the global Terra selector is used in Terra.validates/Terra.it calls

### Changed
- [#3](https://github.com/picimako/terra-support/issues/3): Reorganized documentation and "introduced in \<version>" sections to readme and javadoc

## [0.1.0]

Initial release
