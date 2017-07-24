# Changelog

## 3.6.0
 
### Changes

Fixes black-screen bug on start.
	Removed startup delay

New design for IV preview
	GoIV button automatically adjusts to show IV preview
	GoIV button border color coded for IV results

Fixes multiple crashes related to quick IV preview

Scanning improvements
	Fixes eevee's evolution auto detection on some phone screen resolutions
	Small tweak to HP scanning
	Small tweak to pokemon automatic recognition
	
Added IV combinations to shared results JSON blob for share button

Added clipboard tokens:
	CP missing at level 40 compared to perfect
	Fixed / modified CPMax token to behave as HP+

//Developer change
Refactored out a ----ton of code from the "Pokefly" class into separate objects

## 3.5.0
 
### Changes

  Added Quick IV preview - shows a quick message when you enter a pokemon page
  Bugfix: The appraisal information is now correctly taken into account when predicting future CP in the evolution and powerup screen. 
  Crashfixes for several situations related to custom clipboard settings.
  Automatic appraisal för italian (made by MaicolPain), sorry the update was so slow!


## 3.4.5

### changes
  Makes GoIV work on samsung s8 with fullscreen mode Pogo
  Speeds up parts of the auto appraisal feature (+ small UI tweak)
  You can now copy to clipboard by clicking an IV combination in the list of all possible combinations.
  -a crashfix-
  Changes for scan-related automatic clipboard names:
  added AA-ZZ cp tier token
  added IV-sum token
  added lvl 40 cp token
  ExtendedCPTierToken now defaults to the highest CP evolution

## 3.4.1

### Changes
  Added option for opening Appraisal input on default
  Updated GoIV database to match new Pokemon 2 information
  Fixed a bug causing GoIV to crash when manually changing pokemon
  Tweaked input-window size to allow smaller screen phones to press "appraise" below the input screen.

## 3.4.0

###  Changes
  Appraisal input changed from dropdown to checkboxes
  Implements automatic appraisal (https://www.youtube.com/watch?v=Lbl-O6mT7eQ) - You need to have goiv expanded for it to be in the mode where it searches for appraisal info. This feature is a little slow the first time it reads a sentence, but it will then cache the sentence for the future
  Improved pokemon level detection algorithm for trainer level 30+.
  Several bugfixes

## 3.3.2

### Changes

  Adds base stats for all generation 2 pokemon! (They might not always be auto identified)
  Fixes metapod stats (sorry)
  Adds option to hide the popup of when something has been added to clipboard
  Some small updates to the Clipboard Editor
  The notification icon now shows if GoIV is running or not (The icon gets filled with white when its running)
  Added a delay when starting which prevents some cases of black-screen


## 3.3.1

## Fixes
  Updated for the new pokemon go CP balancing!
  Fixes CP scanning area for some phones
  Fixes candy scanning area for some phones
  Should remove "5" from hp on languages that has hp as "PS"
  Fixed a line in german which was slightly too long


## 3.3.0

###New features
* Adds a user-modifiable clipboard editor (UI is not very pretty, sorry) So now you can create your own custom text for when you scan a pokemon, such as "maxIv - PokemonName"
  You can now pause and play GoIV from the notification.
  Adds "pokespam" calculator - shows how many of a pokemon you can upgrade. (Can be disabled in settings)
  Adds hp estimate to estimate box
  Adds perfection % to estimate box
  Adds a "share" button on the results screen so other apps can use the information of a scanned pokemon. Currently supported by storimõd.
  Enhancements

  New pokemon identification module! - Better at identifying "half" evolutions, such as charmeleon or metapod. Can now also identify pokemon even when they've been renamed!
  If you press to edit Cp or Hp, the keyboard will disappear when you press "check iv".
  Defaults appraise box to closed
  Added a handle for dragging the window when appraisal is open
  Updated some translations
  BugFixes

Fixes some scanning issues with the latest Pokemon Go update which moved around parts of the UI
GoIV stays alive in the background better
Fixes a couple of ocr related crashes

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
