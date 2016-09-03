# Developing for GoIV

## Setting Up
* Download and install [Git] (https://git-scm.com/)
* Set up [Git with GitHub] (https://help.github.com/articles/set-up-git/)
* Download and install [Android Studio] (https://developer.android.com/studio/index.html)
* Set up [Android Development Environment] (https://spring.io/guides/gs/android/)
* [Fork this repo] (https://help.github.com/articles/fork-a-repo/)
* Copy GoIVCodeStyle.xml into the codestyles folder under the Android Studio config/codestyles directory *(create if it does not exist)*
 *  Windows: `\%USERPROFILE%\.{ANDROID_STUDIO_FOLDER}\config\codestyles\`
 *  OS X: `~/Library/Preferences/{ANDROID_STUDIO_FOLDER}/codestyles/`
 *  Linux: `./.{ANDROID_STUDIO_FOLDER}/config/codestyles/`
* Open the Project in Android Studio
* Select Code Style Scheme (`File > Settings > Editor > Code Style > Scheme > Select 'GoIVCodeStyle'`)

## Optional Set-up
Set up Git on Android Studio to automate Git commands (`Configure > Settings > Version Control > Git`)

## Syncing the Fork
[Syncing your fork] (https://help.github.com/articles/syncing-a-fork/) will keep it up to date with the latest commits on the main repo. This will also reduce the chances of getting merge conflicts. Always sync your fork before working on it!

## Contributing with Pull Requests

### Splitting Pull Requests
Please open separate PR's for separate bug fixes or features. It helps us to review your PR's.

### Writing Good Commit Messages
Please take the time to write helpful commit messages. This makes the review of your PR's much easier. It also helps other people to understand the code later on if they are contributing to this project.

You can do so by following these *rules* (taken from this great [article](http://chris.beams.io/posts/git-commit/)):

* Separate subject from body with a blank line
* Limit the subject line to 69 characters
* Use the imperative mood in the subject line
* Wrap the body at 72 characters
* Use the body to explain what and why vs. how

## Generating Signed Release Builds
Follow the instructions under [Sign Your Release Build] (https://developer.android.com/studio/publish/app-signing.html#release-mode) to generate your own keystore.jks file. Place the generated keystore.jks in `<path-to-go-iv>/app`. Make a copy of keystore.properties.sample and rename it to keystore.properties. Change the fields to correspond to your own generated keystore and you should be good to go.
