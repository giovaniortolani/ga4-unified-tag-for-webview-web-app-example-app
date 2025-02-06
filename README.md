<!--
  Smart WebView 7.0

  MIT License (https://opensource.org/licenses/MIT)

  Smart WebView is an Open Source project that integrates native features into
  WebView to help create advanced hybrid applications (https://github.com/mgks/Android-SmartWebView).

  Explore plugins and enhanced capabilities: (https://mgks.dev/app/smart-webview-documentation#plugins)
  Join the discussion: (https://github.com/mgks/Android-SmartWebView/discussions)
  Support Smart WebView: (https://github.com/sponsors/mgks)

  Your support and acknowledgment of the project's source are greatly appreciated.
  Giving credit to developers encourages them to create better projects.
-->

# GA4 Unified Tag for Webview (Web & App) | Example App
This sample app used the [Android Smart WebView](https://github.com/mgks/Android-SmartWebView) as a foundation. Please refer to the original project documentation in the URL provided.

It is an educational material to show how you can integrate the GTM template [GA4 Unified Tag for Webview (Web & App) | Commands Tag](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-commands-tag
) in a real app.

## Getting Started

These instructions will help you get your app copy up and running on your local machine for development and testing purposes.

**[<img src="https://raw.githubusercontent.com/CLorant/readme-social-icons/main/medium/colored/youtube.svg" align="left"> Getting Started with Smart WebView (Video Tutorial)](https://www.youtube.com/watch?v=vE_GsHwspH4&list=PLUvke9lIV6YMGU5XdQ5zOtDOWxslsg6mT&pp=gAQBiAQB)**

### Prerequisites

Project is built on Android Studio and requires minimum Android API 23+ (6.0 Marshmallow) SDK to test run.

### Setup

1. **Download project files**
    * Clone the project (may include untested changes)

        `git clone [https://github.com/mgks/Android-SmartWebView](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app)`

2. **(Important) Download `google-services.json` file from Firebase** ([instructions](#firebase-cloud-messaging))

3. **Load project in Android Studio**

   `File > Open > Browse to Project and Select`

4. **Let Android Studio process the project and download supporting libraries and dependencies**

5. **Try `cleaning` and `rebuilding` the project before run**

   `Build > Clean Project` then `Build > Rebuild Project`

## Configurations

For detailed configuration, check project [Documentation](https://mgks.dev/app/smart-webview-documentation#config).

## Variables used for implementing the Firebase Analytics Webview Communication

You can set/change variables in `SmartWebView.java`.

```java
static String ASWV_APP_URL           = "https://giovaniortolani.github.io/?nativewebview=true";	// default app URL (web or file address)
static boolean POSTFIX_USER_AGENT    = true; // set to true to append USER_AGENT_POSTFIX to user agent
static boolean OVERRIDE_USER_AGENT   = false; // set to true to use USER_AGENT instead of default one
static String USER_AGENT_POSTFIX     = "SWVAndroid"; // useful for identifying traffic, e.g. in Google Analytics
static String CUSTOM_USER_AGENT      = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36"; // custom user-agent
static String ASWV_EXC_LIST          = "mgks.dev,mgks.github.io,github.com,giovaniortolani.github.io";       //separate domains with a comma (,)
```

## Everything that was added or changed for implementing the Firebase Analytics Webview Communication
- [The domains allowed to be opened in the webview.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/SmartWebView.java#L82-L84)
- [Enabled Javascript in the webview via `.setJavaScriptEnabled()`.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L297)
- [Injected the Android Native interface to the DOM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L343-L347)
  - [The Android Native interface definition.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/AnalyticsAndroidWebInterface.java)
- [Injected the Common Native interface to the DOM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L349-L352)
  - [The Common Native interface definition.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/WebviewInterface.java)
- [Injected the Javascript Handler global variable](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L580-L583). Only if not using the GTM template [GA4 Unified Tag for Webview (Web & App) | Firebase Analytics Handler Global Variable Initialization](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-firebase-analytics-handler-global-variable-initialization).

### Webview detection options for GTM
Below you can find a non-exhaustive list of possible options to detect the webview in GTM.
- [Added a query parameter to the default URL to be able to detect the webview in GTM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/SmartWebView.java#L44-L47)
- [Defined a WebViewClient to override URL loading via `.setWebViewClient()`.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L341)
  - [And overrode the URL loading and handling, adding a query parameter to be able to detect the webview in GTM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L609-L618)
- [The User Agent. Added a custom postfix to the User Agent string to be able to detect the webview in GTM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/SmartWebView.java#L86-L92)
- [Added a cookie to the top-level domain to be able to detect the webview in GTM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L354-L362)
- [Added a global variable to the page to be able to detect the webview in GTM.](https://github.com/giovaniortolani/ga4-unified-tag-for-webview-web-app-example-app/blob/master/app/src/main/java/mgks/os/swv/MainActivity.java#L574-L578)
