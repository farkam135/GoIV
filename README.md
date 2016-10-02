# GoIV, Android Project for Pokémon GO.

[![Current Release](https://img.shields.io/github/release/farkam135/GoIV.svg?maxAge=21600 "Current Release")](https://github.com/farkam135/GoIV/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/farkam135/GoIV/total.svg?maxAge=21600 "Downloads")](https://github.com/farkam135/GoIV/releases)
[![Travis Build Status](https://img.shields.io/travis/farkam135/GoIV/master.svg?maxAge=21600 "Travis Build Status")](https://travis-ci.org/farkam135/GoIV)
[![License](https://img.shields.io/github/license/farkam135/GoIV.svg?maxAge=2592000 "License")](LICENSE.md)

### [Release Downloads](https://github.com/farkam135/GoIV/releases)
### [View Changelog](CHANGELOG.md)

## Table of Contents

- [Introduction](#introduction)
- [Screenshots](#screenshots)
- [FAQ](#faq)
- [Community](#community)
- [Special Thanks](#special-thanks)
- [Contributing](#contributing)

## Introduction
GoIV is one of the simplest & fastest IV raters that does not eavesdrop on game servers.

## Screenshots
![Overlays an IV Button](https://i.imgur.com/SxlmeqT.jpg "Overlays an IV Button")
![Scans for Pokémon Info](https://i.imgur.com/0O3d8Vd.jpg "Scans for Pokémon Info")
![Shows Result Popup](https://i.imgur.com/ekBae5R.jpg "Shows Result Popup")
![Advanced Information in Results Popup](https://i.imgur.com/xXr9zzK.jpg "Advanced Information in Results Popup")

## FAQ
- [Is there an iOS version?](#is-there-an-ios-version)
- [Why does GoIV fail to identify my Pokémon?](#why-does-goiv-fail-to-identify-my-pokémon)
- [Why doesn't my arc dot line up perfectly?](#why-doesnt-my-arc-dot-line-up-perfectly)
- [Why doesn't the IV button appear?](#why-doesnt-the-iv-button-appear)
- [Why does Pokémon Go turn black after starting GoIV?](#why-does-pokémon-go-turn-black-after-starting-goiv)
- [Does GoIV break Niantic's terms of use?](#does-goiv-break-niantics-terms-of-use)
- [Can you get banned from Pokémon Go for using GoIV?](#can-you-get-banned-from-pokémon-go-for-using-goiv)
- [Why does GoIV require X permission?](#why-does-goiv-require-x-permission)
- [What are IVs?](#what-are-ivs)

### Is there an iOS version?
No, but GoIV is open source, so someone else might try to port it to iOS. The current developers are not planning on making an iOS version.

### Why does GoIV fail to identify my Pokémon?
GoIV scans the Pokémon name to determine the Pokémon, if you have given your Pokémon a nickname, GoIV will attempt to determine the Pokémon from the Candy portion. If it fails, correcting will improve GoIV future scans on the particular Pokémon.

### Why doesn't my arc dot line up perfectly?
The most common reason for the dot to be misaligned is when the user forgets to increase their trainer level in the app after leveling up in game. You can check your GoIV set trainer level in the persistent notification.

The other reason for the dot to be misaligned is due to different devices having different screen resolutions (some having really unusual resolutions). This feature is always being revised to ensure more devices do not have this issue! You can help by contributing to this [thread] (https://www.reddit.com/r/GoIV/comments/4zi8cd/im_still_trying_to_fix_alignment_issues_i_need/).

### Why doesn't the IV button appear?
The most common reasons are:
* Screen changing apps / features such as screen tinting and dynamic brightness level.
* The phone you're running has a faulty screen capture api (Seems to occasionally affect Cyanogen and some lesser known phone brands)
* GoIV has not been granted permission to draw over other apps.
* GoIV wasn't started.

### Why does Pokémon Go turn black after starting GoIV?
The black screen in Pokémon Go is caused by it being developed on Unity Engine. Sometimes it just doesn't work, so you'll need to restart Pokémon Go. Unfortunately there is nothing we can do to prevent this.

### Does GoIV break Niantic's terms of use?
GoIV is in a gray area, you could argue that it does or does not, depending on how you interpret the wording.

### Can you get banned from Pokémon Go for using GoIV?
It is *possible*, but we don't think it will happen. Niantic could alter their app to scan for other apps running along-side it, but even if they did that, they would probably not go as far as banning users.

### Why does GoIV require X permission?
* Storage - To store the OCR module. (OCR - Optical character recognition, the thing that converts images of text to text.)
* Screen Capture - To identify when to add the overlay, and to scan for Pokémon information.
* Internet (Online version only): To send crash reports and automatically grab new releases.

### What are IVs?
For an explanation in entertaining comic form, click [here](https://www.reddit.com/r/pokemongo/comments/4wnnoj/professor_oak_explains_ivs_in_go/).

Individual Values or IVs are stats that determine how much **extra** power a Pokémon has in addition to the baseline (0% IV). IVs consists of Attack (affects damage of moves), Defense (affects amount of damage received) and Stamina (affects the amount of HP). IVs are calculated using Pokémon level, CP and HP, using these information we are able to calculate the possible IVs for the Pokémon. IVs are fixed and do not change when powering up or evolving, and multiple power up may help in getting. The higher the IVs, the more Combat Points (CP) the Pokémon will have, compared to another Pokémon of the same species & level.

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
