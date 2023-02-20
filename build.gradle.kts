import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.12.0"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.0.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
}

// Set the JVM language level used to compile sources and generate files - Java 11 is required since 2020.3
kotlin {
    jvmToolchain(11)
}

dependencies {
    //https://kotlinlang.org/docs/reflection.html#jvm-dependency
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    //testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0")

    //Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito:mockito-inline:4.2.0") //for using static method mocking
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
    // Set the JVM compatibility versions
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = it
            //https://kotlinlang.org/docs/whatsnew15.html#deprecation-of-jvmdefault-and-old-xjvm-default-modes
            //https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-default/
            kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
                projectDir.resolve("README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider {
            with(changelog) {
                renderItem(
                        getOrNull(properties("pluginVersion")) ?: getLatest(),
                        Changelog.OutputType.HTML,
                )
            }
        })
    }

    test {
        //Required for running tests in 2021.3 due to it not finding test classes properly.
        //See https://app.slack.com/client/T5P9YATH9/C5U8BM1MK/thread/C5U8BM1MK-1639934273.054400
        isScanForTestClasses = false
        include("**/*Test.class")
        //  systemProperty('idea.home.path', '<absolute path to locally cloned intellij-community GitHub repository>')
    }

//    runPluginVerifier {
//        ideVersions.set(listOf('IU-2021.3', 'IU-2022.1', 'IU-2022.2', 'IU-2022.3', 'IU-2023.1'))
//    }
}