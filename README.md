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
sdk.realm=given-by-aag
sdk.environment=test (test(testnet),stage(mainnet),prod(mainnet))
sdk.api.client.reference=given-by-aag
sdk.config.url=given-by-aag
sdk.key=given-by-aag
```

Add following code to your build.gradle file:
```groovy
url properties.getProperty('walletsdk.maven.url')
credentials {
   username = properties.getProperty('walletsdk.maven.username')
   password = properties.getProperty('walletsdk.maven.password')
}

```

Add the following code to your `app/build.gradle` file:
```groovy
implementation 'io.github.aag-ventures:MetaOneSDK:1.4.8'
```

Add mapping to `local.properties` key values to your `app/build.gradle` file:
```groovy
dependencies {
	// M1 SDK auth realm
val sdkRealm = properties.getProperty("sdk.realm")
buildConfigField("String", "SDK_REALM", "\"${sdkRealm}\"")
// M1 SDK environment (dev, test, stage, prod)
val sdkEnvironment = properties.getProperty("sdk.environment") ?: ""
buildConfigField("String", "SDK_ENVIRONMENT", "\"${sdkEnvironment}\"")
// Wallet SDK Key (provided by AAG)
val sdkKey = properties.getProperty("sdk.key") ?: ""
buildConfigField("String", "SDK_KEY", "\"${sdkKey}\"")
// Wallet config url (provided by AAG)
val sdkConfigUrl = properties.getProperty("sdk.config.url") ?: ""
buildConfigField("String", "SDK_CONFIG_URL", "\"${sdkConfigUrl}\"")
// Client reference for API (provided by AAG)
val sdkApiClientReference = properties.getProperty("sdk.api.client.reference") ?: ""
buildConfigField("String", "SDK_API_CLIENT_REFERENCE", "\"${sdkApiClientReference}\"")

buildConfigField("String", "SDK_VERSION", "\"${version}\"")
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
    BuildConfig.SDK_API_CLIENT_REFERENCE
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
val ssoLoginRequest = AuthApiModel.SSOLoginRequest(BuildConfig.SDK_REALM, token)
metaOneSDKManager.login(ssoLoginRequest, callback)
```

Your session is initialized. You can now use all other functions that require Authorization.
Call `metaOneSDKManager.setup()` to initialize user profile data.

## Using SDK functions

The SDK consists of 3 public managers:

**MetaOneSDKManager** - responsible for initialization and session management

**MetaOneSDKApiManager** - responsible for API requests after the user is authenticated

**MetaOneSDKUIManager** - responsible for theming, language, and other app-related tasks

Future feature (In progress) - custom transaction Signing manager (txFees, gasLimits, custom EVM transaction signing, EVM message signing)

**MetaOneSDKManager functions**

- `initialize(sdkConfig: SDKConfig, callback: M1EnqueueCallback<Boolean>? = null)`: Initializes the MetaOne SDK by setting up the app configuration. Provide SDKConfig values mapped from your BuildConfig. You can provide an optional callback to receive the initialization result.

- `setupUserData(callback: M1EnqueueCallback<Pair<UserApiModel.GetProfileResponse?, UserState?>>? = null)`: Sets up the user data by fetching the user profile and user state. This function ensures that the user profile and user state are available for use. You can provide an optional callback to receive the user profile and user state once they are fetched.

- `login(requestData: AuthApiModel.SSOLoginRequest, context: Context, callback: M1EnqueueCallback<AuthApiModel.AuthResponse>)`: Performs the login process by sending an authorization token. The provided callback will receive the login response.

- `refreshSession(callback: M1EnqueueCallback<Boolean>? = null)`: Refreshes the user session to extend the session expiration time. If the session refresh is successful, the provided callback will receive a true value.

- `openWallet()`: For new users, opens the Signature creation flow. If Signature is created, it opens the Wallet activity.

- `startTokenExpirationCountdown()`: Starts the countdown for the token expiration. This function is internally used to track the remaining time until the session expires.

- `cancelTokenExpirationCountdown()`: Cancels the token expiration countdown if it is currently running.

- `setOnTokenExpirationListener(onTokenExpirationListener: OnTokenExpirationListener)`: Sets the listener for token expiration events. You can implement the OnTokenExpirationListener interface to handle token expiration, session activity changes, and token countdown events.

- `getExpireAt(): Long`: Retrieves the expiration timestamp of the user session.

- `getSessionActivityStatus(): SessionActivityStatus`: Retrieves the current session activity status, which can be one of the values defined in the SessionActivityStatus enum.

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

- `getCurrentTheme()`: Retrieves the currently set theme for the MetaOne SDK UI. It returns an Int value representing the theme.
  
- `setCurrentTheme(theme: Int)`: Sets the theme for the MetaOne SDK UI. You need to provide the desired theme as an Int value. IMPORTANT. Currently there are 2 available themes (light, dark). During the integration process you can ask your success manager to add an additional theme by providing a custom color scheme.
  
- `getCurrentLanguage()`: Retrieves the currently set language for the MetaOne SDK UI. It returns a Locale object representing the language.
  
- `setCurrentLanguage(locale: String)`: Sets the language for the MetaOne SDK UI. You need to provide the desired language as a String value, representing the locale.
