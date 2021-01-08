# Changelog

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
