# Build instructions

- Download and install Android studio (https://github.com/farkam135/GoIV/issues/60).
- Install the SDK for Android 7.0 (API version 24) from within android studio (press the SDK manager button in the toolbar, an android face above a box with a down arrow).
- Open the project.
- Install lombok-intellij-plugin (https://github.com/mplushnikov/lombok-intellij-plugin)
- If you get errors because modules are not found, use the SDK manager to add the missing modules.

## Command-line build tool

Android Studio uses the Gradle build tool. It's recommended you invoke it through the `gradlew` wrapper which is part of the project.
You can do, for instance, `./gradlew clean build`.
This requires a JDK for Java 8.
