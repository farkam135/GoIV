# GoIV, Android Project for Pokémon GO.

## Table of Contents

- [What is GoIV](#what-is-goiv)
- [Screenshots](#screenshots)
- [Download](#download)
- [FAQ](#faq)
- [Community](#community)
- [Special Thanks](#special-thanks)
- [Development](#development)

## What is GoIV
GoIV is an android app made for Pokémon GO that allows users to seamlessly calculate their Pokémon's IVs. GoIV does **not** interfere with Pokémon GO or their servers.

## Screenshots
![Alt text](https://i.imgur.com/SxlmeqT.jpg "Overlays an IV Button")
![Alt text](https://i.imgur.com/0O3d8Vd.jpg "Scans for Pokémon Info")
![Alt text](https://i.imgur.com/ekBae5R.jpg "Shows Result Popup")
![Alt text](https://i.imgur.com/xXr9zzK.jpg "Advanced Information in Results Popup")

## Download
### Latest Version: 3.0.1
**[Read the changelog](CHANGELOG.md)**

#### GoIV (Online)
**[Download GoIV (Online) APK](https://github.com/farkam135/GoIV/releases/latest)**  

**Checksums:**  
MD5 - F45110C452B43813D5185E3DDFDE8603  
SHA1 - C688462C15F232EE3C0472E00F77647D866FF975  
SHA-256 - BEF0F40BB711171214C4E641DE4243C3D0FCA288149D468F95850E7960E59B64  

#### GoIV (Offline)
**[Download GoIV (Offline) APK](https://www.reddit.com/r/GoIV/comments/4zvnvh/version_301_hotfix_released/)**

**Checksums:**  
MD5 - 9159BADD0CB4CB29E6D10A6F098D3D3D  
SHA1 - 09A5D65B4D4EF6B92FCF6477B31B9D2E54850673  
SHA-256 - 86859CCFED659FEF2EC6EE91C8454BE2F5B75C1FCB89AE9310AF2567647F73FE  

## FAQ
- [Is there an iOS version?](#is-there-an-ios-version)
- [Why does GoIV fail to identify my Pokémon?](#why-does-goiv-fail-to-identify-my-pokémon)
- [Why doesn't my arc dot line up perfectly?](#why-doesnt-my-arc-dot-line-up-perfectly)
- [Why doesn't the IV button appear?](#why-doesnt-the-iv-button-appear)
- [Does GoIV break Niantic's terms of use?](#does-goiv-break-niantics-terms-of-use)
- [Can you get banned from Pokémon Go for using GoIV?](#can-you-get-banned-from-pokémon-go-for-using-goiv)
- [Why does GoIV require X permission?](#why-does-goiv-require-x-permission)
- [What are IVs?](#what-are-ivs)

### Is there an iOS version?
No, but GoIV is open source, so someone else might try to port it to iOS. The current developers are not planning on making an iOS version.

### Why does GoIV fail to identify my Pokémon?
GoIV scans the Pokémon name to determine the Pokémon, if you have given your Pokémon a nickname, GoIV will attempt to determine the Pokémon from the Candy portion. If it fails, correcting will improve GoIV future scans on the particular Pokémon.

### Why doesn't my arc dot line up perfectly?
The most common reason for the dot to be misaligned is when the user forgets to increase their trainer level in the app after levelling up in game. You can check your GoIV set trainer level in the persistant notification.

The other reason for the dot to be misaligned is due to different devices having different screen resolutions (some having really unusual resolutions). This feature is always being revised to ensure more devices do not have this issue! You can help by contributing to this [thread] (https://www.reddit.com/r/GoIV/comments/4zi8cd/im_still_trying_to_fix_alignment_issues_i_need/).

### Why doesn't the IV button appear?
The most common reasons are:
* Screen changing apps / features such as screen tinting and dynamic brightness level.
* The phone you're running has a faulty screen capture api (Seems to occasionally affect Cyanogen and some lesser known phone brands)
* GoIV has not been granted permission to draw over other apps.
* GoIV wasn't started.

### Does GoIV break Niantic's terms of use?
GoIV is in a gray area, you could argue that it does or does not, depending on how you interpret the wording.

### Can you get banned from Pokémon Go for using GoIV?
It is *possible*, but we don't think it will happen. Niantic could alter their app to scan for other apps running along-side it, but even if they did that, they would probably not go as far as banning users.

### Why does GoIV require X permission?
* Storage - To store the OCR module. (OCR - Optical character recognition, the thing that converts images of text to text.)
* Screen Capture - To identify when to add the overlay, and to scan for Pokémon information.
* Internet (Online version only): To send crash reports and automatically grab new releases.

### What are IVs?
Individual Values or IVs are stats that determine how much **extra** power a Pokémon has in addition to the baseline (0% IV). IVs consists of Attack (affects damage of moves), Defense (affects amount of damage received) and Stamina (affects the amount of HP). IVs are calculated using Pokémon level, CP and HP, using these information we are able to calculate the possible IVs for the Pokémon. IVs are fixed and do not change when powering up or evolving, and multiple power up may help in getting. The higher the IVs, the more Combat Points (CP) the Pokémon will have, compared to another Pokémon of the same species & level.

## Community
Have feedback/questions/comments? [Visit our subreddit](https://www.reddit.com/r/GoIV/)!

## Special Thanks
GoIV would not have been possible without the help and support of the following people:  
* Johan Swanberg
* Stuart Dorff
* Kevin Do
* Tommy Tran

## Development
Want to contribute to the development of GoIV? [Click here to learn how](DEVELOPMENT.md).
