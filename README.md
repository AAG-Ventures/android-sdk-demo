# MetaOne Wallet SDK Integration Guide

This guide will walk you through integrating the MetaOne wallet into your Android app. Provide your users with a secure and convenient way to manage their digital assets with MetaOne Wallet. It is a custody wallet that eliminates the need for private keys, passphrases, or hardware wallets.

## Setup

**Step 1:** Setting up SSH for access to your repository.

After you will be accepted to the SDK integration program, you will be provided with SSH keys required to access secure repositories. If you haven’t received them, please ask your integration success manager to provide you files.

**Step 2:** Adding MetaOne Wallet SDK to your project.

To begin integrating the MetaOne Wallet SDK into your Android application, you need to add the SDK package as a dependency in your project. The SDK package is hosted on Maven Central, making it easy to include in your app using Gradle.

Add the following configuration to your local properties file:
```properties
walletsdk.maven.url=given-by-aag
walletsdk.maven.username=given-by-aag
walletsdk.maven.password=given-by-aag

# setting up SDK environment
sdk.environment=test (test(testnet),stage(mainnet),prod(mainnet))
sdk.api.client.reference=given-by-aag
sdk.api.key.phrase=given-by-client(Key Phrase for API)
sdk.config.url=given-by-client
sdk.key=given-by-aag
sdk.realm=given-by-aag

```

Add following code to your build.gradle file:
```groovy
allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url 'https://www.jitpack.io' }
        maven {
             Properties properties = new Properties()
            properties.load(project.rootProject.file('local.properties').newDataInputStream())

            url properties.getProperty('walletsdk.maven.url')
            credentials {
                username = properties.getProperty('walletsdk.maven.username')
                password = properties.getProperty('walletsdk.maven.password')
            }
        }
    }
}

```

Add the following code to your `app/build.gradle` file:
```groovy
implementation 'io.github.aag-ventures:MetaOneSDK:1.9.3'
```

Add mapping to `local.properties` key values to your `app/build.gradle` file:
```groovy
   defaultConfig {
        ...

        Properties properties = new Properties()
        properties.load(project.rootProject.file('local.properties').newDataInputStream())
        // M1 SDK auth realm
        buildConfigField("String", "SDK_REALM", "\"${properties["sdk.realm"]}\"")
        // M1 SDK environment (dev, test, stage, prod)
        buildConfigField("String", "SDK_ENVIRONMENT", "\"${properties["sdk.environment"]}\"")
        // Client config url
        buildConfigField("String", "SDK_CONFIG_URL", "\"${properties["sdk.config.url"]}\"")
        // Client reference for API (provided by AAG)
        buildConfigField("String", "SDK_API_CLIENT_REFERENCE", "\"${properties["sdk.api.client.reference"]}\"")
        // Client Key Phrase for API
        buildConfigField("String", "SDK_API_KEY_PHRASE", "\"${properties["sdk.api.key.phrase"]}\"")
    }

```

**Step 3:** Initializing SDK

Initialize `MetaOneSDKManager` instance:
```kotlin
metaOneSDKManager = MetaOneSDKManager(this)
```

Map your `BuildConfig` values to `sdkConfig` object:
```kotlin
val sdkConfig = SDKConfig(
    BuildConfig.SDK_REALM,
    BuildConfig.SDK_ENVIRONMENT,
    BuildConfig.SDK_KEY,
    BuildConfig.SDK_CONFIG_URL,
    BuildConfig.SDK_API_CLIENT_REFERENCE,
    BuildConfig.SDK_API_KEY_PHRASE,
    BuildConfig.VERSION_NAME,
    null,
)
```

Initialize MetaOne SDK:
```kotlin
metaOneSDKManager.initialize(sdkConfig)
```

**Step 4:** Creating User Session

To successfully initialize a user session, your backend integration has to be ready first. Your backend should receive an Authorization token during the initialization request.

Initialize the session by calling:
```kotlin
metaOneSDKManager.login(token, this, callback)
```

Your session is initialized. You can now use all other functions that require Authorization.
Call `metaOneSDKManager.setupUserData()` to initialize user profile data.

## Using SDK functions

The SDK consists of 3 public managers:

**MetaOneSDKManager** - responsible for initialization and session management

**MetaOneSDKApiManager** - responsible for API requests after the user is authenticated

**MetaOneSDKUIManager** - responsible for theming, language, and other app-related tasks

Future feature (In progress) - custom transaction Signing manager (txFees, gasLimits, custom EVM transaction signing, EVM message signing)

**MetaOneSDKManager functions**

- `initialize(sdkConfig: SDKConfig, callback: M1EnqueueCallback<Boolean>? = null)`: Initializes the MetaOne SDK by setting up the app configuration. Provide SDKConfig values mapped from your BuildConfig. You can provide an optional callback to receive the initialization result.

- `setupUserData(callback: M1EnqueueCallback<Pair<UserApiModel.GetProfileResponse?, UserState?>>? = null)`: Sets up the user data by fetching the user profile and user state. This function ensures that the user profile and user state are available for use. You can provide an optional callback to receive the user profile and user state once they are fetched.

- `login(token: String, context: Context, callback: M1EnqueueCallback<AuthApiModel.AuthResponse>)`: Performs the login process by sending an authorization token. The provided callback will receive the login response.

- `refreshSession(callback: M1EnqueueCallback<Boolean>? = null)`: Refreshes the user session to extend the session expiration time. If the session refresh is successful, the provided callback will receive a true value.

- `openWallet()`: For new users, opens the Signature creation flow. If Signature is created, it opens the Wallet activity.

- `startTokenExpirationCountdown()`: Starts the countdown for the token expiration. This function is internally used to track the remaining time until the session expires.

- `cancelTokenExpirationCountdown()`: Cancels the token expiration countdown if it is currently running.

- `setOnTokenExpirationListener(onTokenExpirationListener: OnTokenExpirationListener)`: Sets the listener for token expiration events. You can implement the OnTokenExpirationListener interface to handle token expiration, session activity changes, and token countdown events.

- `getExpireAt(): Long`: Retrieves the expiration timestamp of the user session.

- `getSessionActivityStatus(): SessionActivityStatus`: Retrieves the current session activity status, which can be one of the values defined in the SessionActivityStatus enum.

- `sendTransaction()`: Allows to initiate a signing action for sending assets from user's wallet to provided wallet address.

- `logout()`: Logs out the user by clearing the session data, signing out the wallet service.

**MetaOneSDKApiManager functions**

- `getWallets(callback: M1EnqueueCallback<WalletsAPIModel.UserWalletsResponse>)`: Retrieves the user's wallets. The provided callback will receive the wallets response.
  
- `getWallet(walletId: String?, callback: M1EnqueueCallback<WalletsAPIModel.UserWalletResponse>)`: Retrieves a specific wallet based on the wallet ID. You need to provide the wallet ID, and the provided callback will receive the wallet response.
  
- `getCurrencies(callback: M1EnqueueCallback<WalletsAPIModel.UserCurrenciesResponse>)`: Retrieves the user's currencies. The provided callback will receive the currencies response.
  
- `getCurrency(id: String?, callback: M1EnqueueCallback<WalletsAPIModel.UserCurrencyResponse>)`: Retrieves a specific currency based on the currency ID. You need to provide the currency ID, and the provided callback will receive the currency response.
  
- `getNFTs(walletId: String?, searchString: String?, limit: Int = 100, offset: Int = 0, callback: M1EnqueueCallback<WalletsAPIModel.UserNFTsResponse>)`: Retrieves the user's NFTs (Non-Fungible Tokens) based on the wallet ID and optional search parameters. You can provide the wallet ID, a search string, and optional limit and offset values. The provided callback will receive the NFTs response.
  
- `getTransactions(walletId: String?, assetRef: String?, bip44: String?, tokenAddress: String?, page: Int?, offset: Int?, callback: M1EnqueueCallback<TransactionAPIModel.TransactionsResponse>? = null)`: Retrieves the transactions for a specific wallet and optional parameters. You need to provide the wallet ID and can optionally provide the asset reference, bip44 value, token address, page number, and offset. The provided callback will receive the transactions response.
  
- `getTransaction(walletId: String?, chainId: String?, bip44: String?, callback: M1EnqueueCallback<TransactionAPIModel.TransactionResponse>? = null)`: Retrieves a specific transaction based on the wallet ID, chain ID, and bip44 value. You need to provide the wallet ID, chain ID, and bip44 value, and the provided callback will receive.
  
- `getUserContacts(callback: M1EnqueueCallback<ContactsApiModel.ContactsResponse>)`: Retrieves the user's contacts from the address book. The provided callback will receive the contacts response.
  
- `getUserContactWithId(id: String, callback: M1EnqueueCallback<ContactsApiModel.ContactResponse>)`: Retrieves a specific contact based on the contact ID. You need to provide the contact ID, and the provided callback will receive the contact response.

**MetaOneSDKUIManager functions**

- `getColorsScheme()`: Retrieves the currently set colors for the MetaOne SDK UI. It returns colors
  
- `setColorsScheme(colors: ColorsScheme)`: Sets the colors for the MetaOne SDK UI. You need to provide the desired colors as an M1Color.ColorsScheme object.

```
{
        "alwaysWhite": "#FFFFFF",
        "alwaysBlack": "#101111",
        "primary": "#386CF3",
        "primary80": "#386CF3CC",
        "primary60": "#386CF399",
        "primary40": "#386CF366",
        "primary20": "#417FF6CC",
        "secondary": "#604EFF",
        "secondary80": "#604EFFCC",
        "secondary60": "#604EFF99",
        "secondary40": "#604EFF66",
        "secondary20": "#604EFF33",
        "secondary15": "#604EFF26",
        "primaryButtonBg": "#386CF3",
        "primaryButtonBgDisabled": "#386CF360",
        "primaryButtonText": "#FFFFFF",
        "secondaryButtonBg": "#417FF6CC",
        "secondaryButtonBgDisabled": "#417FF633",
        "secondaryButtonText": "#386CF3",
        "errorButtonBg": "#FFFFFF",
        "errorButtonText": "#D93F33",
        "green": "#1BAC3F",
        "greenBg": "#B7E8C3",
        "yellow": "#DEA511",
        "yellowBg": "#F0E29A",
        "yellow15": "#DEA51126",
        "red": "#D93F33",
        "redBg": "#F5B9B5",
        "blue": "#386CF3",
        "blueBg": "#C6DAFF",
        "wireframes": "#BDC2CA",
        "wireframesLight": "#D8E0E5",
        "gradientLight": "#E0F9FD",
        "gradientViolet": "#6851F5",
        "gradientBlue": "#7999FE",
        "average": "#F7931A",
        "background": "#F0F2F4",
        "background20": "#F0F2F433",
        "white": "#FFFFFF",
        "white20": "#FFFFFF33",
        "white50": "#FFFFFF80",
        "white80": "#FFFFFFCC",
        "black": "#101111",
        "black80": "#101111CC",
        "black60": "#10111199",
        "black40": "#10111166",
        "black20": "#10111133",
        "black15": "#10111126",
        "black10": "#1011111A",
        "black5": "#1011110D",
        "pin": "#0066FF"
}
```
- `getCurrentLanguage()`: Retrieves the currently set language for the MetaOne SDK UI. It returns a Locale object representing the language.
  
- `setCurrentLanguage(locale: String)`: Sets the language for the MetaOne SDK UI. You need to provide the desired language as a String value, representing the locale.
