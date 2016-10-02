# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Unless you want to volunteer to upload the mappings file to crashlytics leave this app unobfuscated ;)
-dontobfuscate

-keep class javax.** { *; }
-keep class org.** { *; }
-keep class com.** {*;}
-dontwarn okio.**

# For Guava
-dontwarn sun.misc.Unsafe
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn java.lang.ClassValue
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
