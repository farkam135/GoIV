GoIV, android project for Pokemon Go.
=====================

**Table of Contents**

- [What is GoIV](#what-is-goiv)
- [Images](#images)
- [Download](#download)
- [FAQ](#faq)
- [Community](#community)
- [Special thanks](#special-thanks)

#What is GoIV
GoIV is an android app made for Pokemon GO that allows users to seamlessly calculate their Pokemon's IVs. GoIV does <b>not</b> interfere with Pokemon GO or their servers.

#Images
![Alt text](https://i.imgur.com/bvRfmZV.jpg "Seamlessly Overlays an IV Button")
![Alt text](https://i.imgur.com/aNHUEVI.jpg "Seamlessly Overlays an IV Button")

#Download
**Download GoIV (Online) APK: https://github.com/farkam135/GoIV/releases**  
Latest Version: 2.2.0

**Checksums:**  
MD5 - 57F2C9628389E787BA2F1B095C9261EC  
SHA1 - 41CA160E5F7F59D84248A6916AE986F8C2E8C1BD  
SHA-256 - 2B4C3102ED9572E8194CDFE2D7B6E4AF28EE93B54CFE4E4921736B7645515D3D  

*Download GoIV (Offline) APK: https://www.reddit.com/r/GoIV/comments/4z5qxz/goiv_220_released/*

#Changelog

###New features

* In-app update checker! The application will check the github repo for any newer version, and ask if you want to download an update, if a new update comes. (This feature is only for the internet version of the app)
* Settings menu! Toggle if pokemon go launches automatically on start, if you want the app to skip the input field and show results directly, delete screenshots in battery saver mode & if the app should copy info to clipboard.
* Now on tap support - If you're running in battery saver mode, you can use now on tap instead of taking screenshots. Press the share button, and share with GoIV.

###Improvements

* Improved CP scanning accuracy (several fixes & tweaks, for example it no longer occasionally reads the p in cp as a 3)
* Pokemon will be identified by the candy name if the pokemon has been renamed. (Seemingly less random recognition, the app will still use Names for recognition, so run scan on default name pokemon for best result)
* You can now go back from the result screen to fix any input (works really well with the instant-scan setting)
* Speed improvements (If you think "speed improvements" is obscure, look at the implementation improvements on Github!)

###Bug fixes

* GoIV no longer crashes if the CP value is covered when the scan is initiated. (Gastly caused so many crashes)
* No longer crashes if a user exits portrait mode
* GoIV can now tell the difference between male and female nidoran. (scans the pokemon and checks if it's purple or white-ish)
* Water pokemon no longer bug out the app when it tries to read the CP - No more 114833 CP vaporeons.
* Arc detection support for 480p screens (the start button is still covered on some devices though, fix incoming soon)

#FAQ

- [Is there an iOS version?](#is-there-an-ios-version)
- [Why does the app fail to scan my Pokemon type?](#why-does-the-app-fail-to-scan-my-pokemon-type)
- [Why doesn't my arc dot line up perfectly?](#why-doesn't-my-arc-dot-line-up-perfectly)
- [Why doesn't the IV button appear?](#why-doesn't-the-iv-button-appear)
- [Does this application break Niantics terms of use?](#does-this-application-break-niantics-terms-of-use)
- [Can you get banned from Pokémon Go for using GoIV?](#can-you-get-banned-from-pokémon-go-for-using-goivd)
- [Why does GoIV require X permission?](#why-does-goiv-require-x-permission)
- [What are IVs?](#what-are-ivs)

###Is there an iOS version?

No, but the application is open source, so someone else might try to recreate it in iOS. The current developers are not planning on making an iOS version.

###Why does the app fail to scan my Pokemon type?

The applicaiton scans the pokemon name to determine pokemon type, so if you've given your pokemon a nickname, it's going to fail.

###Why doesn't my arc dot line up perfectly?

If it's just a couple of pixels wrong, then it's fine. It hardly lines up 'perfectly' on any device.

Usually when the dot is misaligned, it turns out the user forgot to increment their level in the app. You can see what level the application thinks your trainer is in the notification, it it's wrong, change it in the app!

If the dot still doesn't fit, then you might have a device with an unusual resolution, and it would be good if the application was updated to accommodate that.

###Why doesn't the IV button appear?

The most common reasons are:

Screen changing apps / features such as screen tinting and dynamic brightness level.

The phone you're running has a faulty screen capture api (Seems to occasionally affect Cyanogen and some lesser known phone brands)

The application has not been granted permission to draw over other apps.

The application wasn't started.

###Does this application break Niantics terms of use?

This application is in a gray area, you could argue that the application does or does not, depending on how you interpret the wording.

###Can you get banned from Pokémon Go for using GoIV?

It's 'possible', but we don't think it will happen. Niantic could alter their app to scan for other apps running along-side it, but even if they did that, they probably wont go as far as banning users.

###Why does GoIV require X permission?

Storage:
To store the OCR module. (OCR - Optical character recognition, the thing that converts images of text to text.)

Screen capture:
To identify when to add the overlay, and to read the pokemon information.

###What are IVs?

Basically stats that determine how much extra power a Pokemon has, compared to the baseline. The better the IVs, the more hp/attack/defence (CP) the pokemon will have, compared to another Pokemon of the same species & level.

#Community
Have feedback/questions/comments? Visit our subreddit: https://www.reddit.com/r/GoIV/

#Special Thanks
GoIV would not have been possible without the help and support of the following people:  
 - Johan Swanberg  
 - Stuart Dorff  
 - Kevin Do  
 - Tommy Tran  

# Development

For info on developing GoIV, see [DEVELOPMENT.md](DEVELOPMENT.md).
