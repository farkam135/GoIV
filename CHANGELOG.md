# Changelog

## 3.1.0

### Features & Improvements
* New layout for when only one IV combination is possible
* Tweaked layout and sized of input dialog
* Show all ivs is color coded & layout improved
* Hides "refine by power up" automatically if it cant be used

### Stability and Speed Improvements
* Fixes several crash/hang bugs which caused the application to become unresponsive
* Fixed scanning of 10 cp or 10 hp pokemon
* Fixed crash related to screenshot setting on android 4.4
* Bugfix in pokemon name correction learning (ignore cancel)
* Fixed typos

---

## 3.0.1

### Bug Fixes
* Fixed CP estimate calculations
* Improved arc detection
* Fixed candy detection for French/Spanish/Italian
* Theming improvements (this includes the invisible checkboxes fix)
* Fixed crashes at app shutdown

---

## 3.0.0

### New Features
* Appraisal checkboxes
* New results screen UI
* Can view all IV combinations
* Narrow down possible IVs by upgrading and getting intersection
* Get Pokémon CP at any level and evolution prediction (including levels above trainer max)

### Enhancements
* App remembers user OCR corrections
* OCR cache speeds up ocr in some situations
* IV percentage results are color coded
* Portuguese and French localization
* Language fixes

### Stuff
* Some stuff is green
* Minor text fixes (for real)

### Behind the Scenes
* Speed improvements
* Code refactoring
* Several crashfixes & stability improvements

---

## 2.2.0

### New Features
* In-app update checker! The application will check the GitHub repo for any newer version, and ask if you want to download an update, if a new update comes. (This feature is only for the internet version of the app)
* Settings menu! Toggle if pokemon go launches automatically on start, if you want the app to skip the input field and show results directly, delete screenshots in battery saver mode & if the app should copy info to clipboard.
* Now on tap support - If you're running in battery saver mode, you can use now on tap instead of taking screenshots. Press the share button, and share with GoIV.

### Improvements
* Improved CP scanning accuracy (several fixes & tweaks, for example it no longer occasionally reads the p in cp as a 3)
* Pokemon will be identified by the candy name if the pokemon has been renamed. (Seemingly less random recognition, the app will still use Names for recognition, so run scan on default name pokemon for best result)
* You can now go back from the result screen to fix any input (works really well with the instant-scan setting)
* Speed improvements (If you think "speed improvements" is obscure, look at the implementation improvements on GitHub!)

### Bug Fixes
* GoIV no longer crashes if the CP value is covered when the scan is initiated. (Gastly caused so many crashes)
* No longer crashes if a user exits portrait mode
* GoIV can now tell the difference between male and female nidoran. (scans the pokemon and checks if it's purple or white-ish)
* Water pokemon no longer bug out the app when it tries to read the CP - No more 114833 CP vaporeons.
* Arc detection support for 480p screens (the start button is still covered on some devices though, fix incoming soon)

---

## 2.1.0

### New Features
* Shows CP projection for evolution
* Shows CP projection for the max level you can currently make your pokemon

### Improvements
* Added Crashlytics to check for crashes (Internet permissions added)

### Bug Fixes
* No longer copies % range on scan, will be re-added as an option in the future
* Reverted languages to only supported
* Improved battery saver mode (should work on more devices!)
* Performance improvements
* Fixed needed candy/stardust formula

---

## 2.0.0

### New Feature
* Android KitKat Support with Battery Saver

### Improvements
* Now copies "lowpercent - highpercent" to clipboard after scan
* Now starts PoGo after starting GoIV
* New notification icon
* Performance Improvements

### Bug Fixes
* Remove Billing Permission
* Fixed arc alignment for trainer's level >= 30
* Remove "Scanning" text

---

## 1.3.0

### New Feature
* You now get to see how much candy / stardust it would cost to max the level of a pokemon you analyzed.

### Improvements
* App version text added in the app.
* The app is now open sourced!

### Bug Fixes
* Non-working donate button removed, credits page removed.
* Fixed visual bug where if you manually changed the pokemon type, the result would still say whatever pokemon was automatically detected.
