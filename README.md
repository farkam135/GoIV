# GoIV, Android Project for Pokémon GO.

[![Current Release](https://img.shields.io/github/release/farkam135/GoIV.svg?maxAge=21600 "Current Release")](https://github.com/farkam135/GoIV/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/farkam135/GoIV/total.svg?maxAge=21600 "Downloads")](https://github.com/farkam135/GoIV/releases)
[![Travis Build Status](https://img.shields.io/travis/farkam135/GoIV/master.svg?maxAge=21600 "Travis Build Status")](https://travis-ci.org/farkam135/GoIV)
[![License](https://img.shields.io/github/license/farkam135/GoIV.svg?maxAge=2592000 "License")](LICENSE.md)

### [Release Downloads](https://github.com/farkam135/GoIV/releases)
### [Google Play link](https://play.google.com/store/apps/details?id=org.opensource.goiv&hl=en)

## Table of Contents

- [Introduction](#introduction)
- [Screenshots](#screenshots)
- [FAQ](#faq)
- [Community](#community)
- [Special Thanks](#special-thanks)
- [Contributing](#contributing)

## Introduction
GoIV is one of the simplest & fastest IV calculators that does not eavesdrop on game servers.
[Here's a video](https://www.youtube.com/watch?v=gxaI7231HtE) of approximately how it looks. (The UI might change some between versions.)

## Screenshots
![Overlays an IV Button (with IV preview)](https://i.imgur.com/3Q3AhuH.png "Overlays an IV Button (with IV preview)")
![Scans for Pokémon Info](https://i.imgur.com/iWvt2eV.png "Scans for Pokémon Info")
![Shows Result Popup](https://i.imgur.com/EZghuti.png "Shows Result Popup")
![Advanced Information in Results Popup](https://i.imgur.com/p69tGc7.png "Advanced Information in Results Popup")

## FAQ

### Is there an iOS version?
No, and iOS doesn't have several of the APIs required to work on iOS as it does on android (App overlays, Screen API).

### Why does GoIV fail to identify my Pokémon / give me an error?
The most common reason for the dot to be misaligned is when the user levels up their trainer level, and doesn't increase it in the
GoIV app. You can check your GoIV set trainer level in the persistent notification.

### Why doesn't the IV button appear?
The most common reasons are:
* Screen changing apps / features such as screen tinting and dynamic brightness level makes GoIV fail at recognizing the pokemon screen.
* The phone you're running has a faulty screen capture api (Seems to occasionally affect Cyanogen and some lesser known phone brands)
* GoIV has not been granted permission to draw over other apps.
* GoIV wasn't started / is running in screenshot mode.

### How do I export my pokemon to PokeBattler?
Scan the pokemon by pressing the iv-button in the lower left corner. Press "check iv", press the "Moveset" tab, scroll down, and export.

### Does GoIV break Niantic's terms of use? Can I get banned?
Our goal is to create an app that's compliant with the terms of use, by only using information already available to the player,
and only performing calculations the player could do by hand.  That said, technically Niantics terms allows them to ban anyone for
any reason. To our knowledge, no GoIV user has ever been banned for using GoIV.

### Why does GoIV require X permission?
* Storage - To store the OCR module. (OCR - Optical character recognition, the thing that converts images of text to text.)
* Screen Capture - To identify when to add the overlay, and to scan for Pokémon information.
* Internet (Online version only): To send crash reports and automatically grab new releases.

### What are IVs? Do they matter?
Individual Values or 'IVs' are stats that determine how much **extra** power a Pokémon has in addition to the baseline (0% IV). IVs consists of Attack (affects damage of moves), Defense (affects amount of damage received) and Stamina (affects the amount of HP). IVs are calculated using Pokémon level, CP and HP, using these information we are able to calculate the possible IVs for the Pokémon. IVs are fixed and do not change when powering up or evolving, and multiple power up may help in getting. The higher the IVs, the more Combat Points (CP) the Pokémon will have, compared to another Pokémon of the same species & level.
For example, the base stats of a pokemon could be 100 attack, 100 defence, and 100 stamina. That would mean that all pokemon of that species has at least 100 in each stat, but with added bonuses, could have up to 115 of all stats. A perfect IV species would therefor have 115 attack, 115 defence, and 115 stamina.

## Community
Have feedback/questions/comments? Post questions in the [subreddit](https://www.reddit.com/r/GoIV/) or visit our [Discord channel](https://discord.gg/y6BvF5D)!

## Special Thanks
GoIV would not have been possible without the help and support of the following people:  
* Johan Swanberg
* Stuart Dorff
* Kevin Do
* Tommy Tran
* [And further thanks to all who contribute to the project on GitHub](https://github.com/farkam135/GoIV/graphs/contributors)

## Contributing
Want to contribute to the development of GoIV? [Click here](CONTRIBUTING.md) to learn how.
