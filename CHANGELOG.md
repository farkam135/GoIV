# Changelog

## 3.2.0

### Features
* Major battery improvements: **The screen scanning logic will now only activate
  after you touch the screen**. So instead of scanning every 750 milliseconds, it
  will only scan a couple of times with a short delay when you touch the screen.
  If you tap it too fast, it'll wait for you to be done tapping before scanning.
  So, gym fights should cause zero scanning.

  Hence:
  **Tap the screen once if the IV button doesn't show up when it should.**

* More appraisal narrowing: You can now use all the information given by the
  team leader to narrow down the the iv possibilities for your pokemon. You'll
  have to choose your team in the app start screen to get the right phrases to
  pick from. When you expand the appraisal box now, the overlay will move to the
  top of your screen so that you can click the appraisal info while the overlay
  is active.

* Easier to correct pokemon scan: Identified pokemon dropdown only shows
  evolution line as identified from candy If evolution line is wrongly
  identified, can search for the pokemon with keyboard.

* UI improvement for level slider in evolution and powerup result box: There's
  now a yellow background to show the limit of where your current trainer is too
  low to level up your pokemon.

* Notification actions: Now GoIV can be stopped from the notification itself.

### Quality of life changes

* Updated translation strings

* Cleaner scan error handling: If a scan error occurs, the field will be left
  blank to make it easier for you to see and deal with.

* Full IV possibilities list is now sorted by perfection %

* "Show all IV combinations" hidden if no / too many IV combinations.

### Bugfixes
* Screenshot mode should now work on most (all?) devices!
* Fix crash on levels 39/40.
* Overlay moves up when you input stuff.
* Crashfixes on some OCR error scans.
* Refine by power up appears correctly when pokemon is evolved.
* And more.

### Behind the scenes
* Lots of code cleanup (Though we're not done yet)

### Known bugs
* At level 30, level 30 pokemons are detected as level 30.5, and this can cause
  spurious failures to find combinations or incorrect results. Adjusting the
  level to 30 manually gives correct calculation results. See
  screenshots [here](https://github.com/farkam135/GoIV/issues/455) to see the
  results appears.

---

## 3.1.0

### Features & Improvements
* New layout for when only one IV combination is possible
* Tweaked layout and sized of input dialog
* Show all IVs is color coded & layout improved
* Hides "refine by power up" automatically if it can't be used

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
