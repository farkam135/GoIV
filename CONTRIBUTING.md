# Contributing to GoIV Development

## Setting Up Android Studio & Git
* Download and install [Git] (https://git-scm.com/)
* Set up [Git with GitHub] (https://help.github.com/articles/set-up-git/)
* Download and install [Android Studio] (https://developer.android.com/studio/index.html)
* Set up [Android Development Environment] (https://spring.io/guides/gs/android/)

In instructions below we'll refer to IDEA's Settings dialog. We list it as
reachable under `File > Settings`, but on Mac OS X it is found instead under
`Android Studio > Preferences`.

## Project Set-up for GoIV (Required)
* [Fork GoIV Repo] (https://help.github.com/articles/fork-a-repo/)
* Copy `GoIVCodeStyle.xml` into the codestyles folder under the Android Studio preferences folder *(create codestyles folder if it does not exist)*
 *  Windows: `\%USERPROFILE%\.{ANDROID_STUDIO_FOLDER}\config\codestyles\`
 *  OS X: `~/Library/Preferences/{ANDROID_STUDIO_FOLDER}/codestyles/`
 *  Linux: `./.{ANDROID_STUDIO_FOLDER}/config/codestyles/`
* Open the Project in Android Studio
* Select Code Style Scheme (`File > Settings > Editor > Code Style > Scheme > Select 'GoIVCodeStyle'`)

*__{ANDROID_STUDIO_FOLDER}__ varies on the Android Studio version installed. Read more [here](http://tools.android.com/tech-docs/configuration).*

## Updating Codestyle Definition
If `GoIVCodeStyle.xml` has changed, close Android Studio, copy our file over the copy in Android Studio settings as above, and reopen Android Studio. The new setting should be loaded.

## Optional Set-up

### Discuss in discord channel
Talking about your idea / contribution with the other developers before creating a PR can save enourmous amounts of time, as many issues have already been discussed, and your idea might have already been rejected due to previous issues. Please ask / talk about any idea you have before you create it!
https://discord.gg/Z2QKkZ7

### CheckStyle Plugin
This plugin checks the codebase for violations of coding style.
* Install CheckStyle Plugin (`File > Settings > Plugins > Browse Repositories > Search 'CheckStyle-IDEA' > Install`)
* Restart Android Studio if requested.
* Configure it with the coding style we use for GoIV:
  - Navigate `File > Settings > Other Settings > Checkstyle > Configuration File`
  - then add an entry with + button under the list
  - Select 'Use a local Checkstyle file' in the new dialog
  - Select 'checkstyle/checkstyle-rules.xml' from the GoIV folder, pick a name,
    add the rules and enable them.

This is especially important to send pull requests.

### Lombok Plugin
GoIV project uses Lombok Plugin to automatically generate various methods (i.e. Getter, Setters). Without this plugin, `Unable to resolve method` errors will show, but code will compile and build as per normal.
* Close the Project *(required to enable annotation processing)*
* Install Lombok Plugin (`File > Settings > Plugins > Browse Repositories > Search 'Lombok Plugin' > Install`)
* Restart Android Studio
* Enable Annotation Processing (`File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`)

### Git on Android Studio
Set-up Git on Android Studio to automate Git commands. For developers who are *lazy* and prefer one-click solutions.
* Set up Git on Android Studio (`File > Settings > Version Control > Git`)

## Syncing your Fork
[Syncing your fork] (https://help.github.com/articles/syncing-a-fork/) will keep it up to date with the latest commits on the main repo. This will also reduce the chances of getting merge conflicts. Always sync your fork before working on it!

## Contributing with Pull Requests

### Ensure Commits Follow Coding Style
Use Android Studio autoformatting and check with CheckStyle that your PR does not add coding style violations.

### Splitting Pull Requests
Please open separate PR's for separate bug fixes or features. It helps us to review your PR's.

### Closing Related Resolved Issues Automatically
When your PR resolves a specific issue or issues in the *Issue* tracker, include the specific phrasing that will automatically close the related issue(s) it resolves somewhere in your PR message. The specific phrasing is a variation of 'fix', 'resolve', or 'close' followed by the issue number, e.g.: `Fixes #123` or `These changes resolve #321`. For more info, see this [GitHub help article](https://help.github.com/articles/closing-issues-via-commit-messages/).

### Writing Good Commit Messages
Please take the time to write helpful commit messages. This makes the review of your PR's much easier. It also helps other people to understand the code later on if they are contributing to this project.

You can do so by following these *rules* (taken from this great [article](http://chris.beams.io/posts/git-commit/)):

* Separate subject from body with a blank line
* Limit the subject line to 69 characters
* Use the imperative mood in the subject line
* Wrap the body at 72 characters
* Use the body to explain what and why vs. how

## Preparing for Release
Update the following values found in `build.gradle (Module: app)` before generating release builds:
* versionCode (Increment of 1)
* versionName (Based on [Semantic Versioning 2.0](http://semver.org/))

## Generating Signed Release Builds
* Follow the instructions under [Sign Your Release Build] (https://developer.android.com/studio/publish/app-signing.html#release-mode) to generate your own keystore.jks file
* Place the generated keystore.jks in `<path-to-go-iv>/app`
* Make a copy of keystore.properties.sample and rename it to keystore.properties
* Change the fields to correspond to your own generated keystore and you should be good to go
