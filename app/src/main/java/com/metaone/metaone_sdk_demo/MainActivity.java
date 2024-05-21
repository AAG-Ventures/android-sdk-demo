package com.metaone.metaone_sdk_demo;

import static ventures.aag.metaonesdk.helpers.IntervalKt.currentTimeUnix;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.metaone.metaone_sdk_demo.components.base.BaseActivity;

import kotlin.Pair;
import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.OnTokenExpirationListener;
import ventures.aag.metaonesdk.models.SDKConfig;
import ventures.aag.metaonesdk.models.SessionActivityStatus;
import ventures.aag.metaonesdk.models.User;
import ventures.aag.metaonesdk.models.api.UserApiModel;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;


public class MainActivity extends BaseActivity implements WalletsAPIModel.OnWalletEventListener {

    private LinearLayout unauthorizedLayout;
    private Button loginButton;

    private Boolean isSDKInitialized = false;

    private LinearLayout authorizedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButtonActions();
        initializeSDK();
        setColors();
        metaOneSDKManager.addWalletCreateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        metaOneSDKManager.removeWalletCreateListener(this);
    }

    protected void setColors() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.textView)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.expires_at)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.email)).setTextColor(colors.getBlack());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isSDKInitialized) {
            startSessionTracker();
        }
    }

    private void initializeSDK() {
        SDKConfig sdkConfig = new SDKConfig(
                BuildConfig.SDK_REALM,
                BuildConfig.SDK_ENVIRONMENT,
                BuildConfig.SDK_CONFIG_URL,
                BuildConfig.SDK_API_CLIENT_REFERENCE,
                BuildConfig.SDK_API_KEY_PHRASE,
                BuildConfig.VERSION_NAME
        );
        metaOneSDKManager.initialize(sdkConfig,
                new M1EnqueueCallback<>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        super.onSuccess(result);
                        startSessionTracker();
                        isSDKInitialized = true;
                    }

                    @Override
                    public void onFailure(String string) {
                        super.onFailure(string);
                        Toast.makeText(MainActivity.this, "SDK initializing failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ErrorResponse t) {
                        super.onError(t);
                        Toast.makeText(MainActivity.this, "SDK initializing failed " + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startSessionTracker() {
        metaOneSDKManager.setOnTokenExpirationListener(new OnTokenExpirationListener() {
            @Override
            public void onSessionActivityChange(@NonNull SessionActivityStatus status) {
                Log.d("MetaOneSDK", "Session activity changed to " + status.toString());
            }

            @Override
            public void onTokenCountdown(long secondsLeft) {
                TextView countdown = findViewById(R.id.expires_at);
                countdown.setText("Token expires in " + secondsLeft + " seconds");
            }

            @Override
            public void onTokenExpiration() {
                Log.d("MetaOneSDK", "Token expired");
            }

        });
    }

    private Boolean isSignatureSet () {
      Boolean isSet =  metaOneSDKManager.isSignatureSet();
      if(!isSet){
          Toast.makeText(getApplicationContext(), "Please create your signature", Toast.LENGTH_LONG).show();
      }
      return isSet;
    }
    private void addButtonActions() {
        authorizedLayout = findViewById(R.id.authorized_layout);
        loginButton = findViewById(R.id.login_button);
        unauthorizedLayout = findViewById(R.id.unauthorized_wrapper);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button openWalletButton = findViewById(R.id.open_wallet_btn);
        openWalletButton.setOnClickListener(v -> {
            try {
                metaOneSDKManager.openWallet();
            } catch (Exception e) {
                CharSequence text = e.getMessage();
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
            }

        });
        Button customSendTxButton = findViewById(R.id.send_custom_tx_btn);
        customSendTxButton.setOnClickListener(v -> {
            if (metaOneSDKManager.isSignatureSet()) {
                Intent intent = new Intent(MainActivity.this, SignCurrencySendTransactionActivity.class);
                startActivity(intent);
            }
        });
        Button customSignTxButton = findViewById(R.id.sign_custom_tx_btn);
        customSignTxButton.setOnClickListener(v -> {
            if (metaOneSDKManager.isSignatureSet()) {
                Intent intent = new Intent(MainActivity.this, SignCustomTransactionActivity.class);
                startActivity(intent);
            }
        });
        Button apiTestButton = findViewById(R.id.api_testing_btn);
        apiTestButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ApiTestingActivity.class);
            startActivity(intent);
        });

        Button refreshSession = findViewById(R.id.refresh_session_button);

        refreshSession.setOnClickListener(v -> {
            metaOneSDKManager.refreshSession(new M1EnqueueCallback<>() {
                @Override
                public void onSuccess(Boolean result) {
                    super.onSuccess(result);
                    Toast.makeText(MainActivity.this, "Session refreshed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String string) {
                    super.onFailure(string);
                    Toast.makeText(MainActivity.this, "Session refresh failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(ErrorResponse t) {
                    super.onError(t);
                    Toast.makeText(MainActivity.this, "Session refresh failed " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button languageButton = findViewById(R.id.language);
        languageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangeLanguageActivity.class);
            startActivity(intent);
        });
        Button themeButton = findViewById(R.id.theme);
        themeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangeThemeActivity.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            metaOneSDKManager.logout();
            onChangeIsAuthorized();
        });
    }

    private void onChangeIsAuthorized() {
        Boolean isAuthorized = metaOneSDKManager.getSessionActivityStatus() != SessionActivityStatus.UNAUTHORISED;
        authorizedLayout.setVisibility(isAuthorized ? View.VISIBLE : View.GONE);
        unauthorizedLayout.setVisibility(isAuthorized ? View.GONE : View.VISIBLE);
        if (isAuthorized) {
            // Check if expires at is greater than current time
            Long expireAt = metaOneSDKManager.getExpireAt();

            M1EnqueueCallback callback = new M1EnqueueCallback<Pair<UserApiModel.GetProfileResponse, User.UserState>>() {
                @Override
                public void onSuccess(Pair<UserApiModel.GetProfileResponse, User.UserState> response) {
                    TextView email = findViewById(R.id.email);
                    email.setText("Email: " + response.component1().getProfile().getEmail());
                }
            };
            if (expireAt < currentTimeUnix() + 5) {
                metaOneSDKManager.refreshSession(new M1EnqueueCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        super.onSuccess(result);
                        metaOneSDKManager.setupUserData(callback);
                    }
                });
            } else {
                metaOneSDKManager.setupUserData(callback);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onChangeIsAuthorized();
        setColors();
    }

    @Override
    public void onWalletCreated(WalletsAPIModel.Wallet wallet) {
        Log.v("WALLET CREATED", wallet.toString());
    }
}
