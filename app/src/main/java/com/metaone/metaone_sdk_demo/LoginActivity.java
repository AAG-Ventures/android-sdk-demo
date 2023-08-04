package com.metaone.metaone_sdk_demo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.metaone.metaone_sdk_demo.api.ApiClient;
import com.metaone.metaone_sdk_demo.api.ApiService;
import com.metaone.metaone_sdk_demo.api.ApiUtil;
import com.metaone.metaone_sdk_demo.api.response.SampleSSOLoginResponse;
import com.metaone.metaone_sdk_demo.components.base.BaseActivity;
import com.metaone.metaone_sdk_demo.utils.PreferenceUtils;


import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.MetaOneSDKManager;
import ventures.aag.metaonesdk.models.SessionActivityStatus;
import ventures.aag.metaonesdk.models.api.AuthApiModel;


public class LoginActivity extends BaseActivity {
    private ApiService apiService;

    MetaOneSDKManager metaOneSDKManager;

    PreferenceUtils preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metaOneSDKManager = new MetaOneSDKManager(this);
        setContentView(R.layout.activity_login);
        TextView textView = findViewById(R.id.header_title);
        textView.setText("Login");
        preference = new PreferenceUtils(this);
        apiService = ApiClient.getClient();
        addButtonActions();
        setUpKeyboard();
    }

    protected void setUpKeyboard() {
        View layout = findViewById(R.id.root_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private void addButtonActions() {
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> handleLogin());

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void reset() {
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setEnabled(true);

        Button confirm2faButton = findViewById(R.id.confirm_2fa_button);
        confirm2faButton.setEnabled(false);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void handleLogin() {
        TextInputEditText emailInput = findViewById(R.id.login_email_input);
        String email = String.valueOf(emailInput.getText());

        Map<String, String> requestData = Map.of(
                "email", email,
                "password", "123456"
        );
        RequestBody loginRequest = ApiUtil.createRequestBody(requestData);
        Call<SampleSSOLoginResponse> loginCall = apiService.sampleSsoLogin(loginRequest);
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SampleSSOLoginResponse> call, Response<SampleSSOLoginResponse> response) {
                if (response.isSuccessful()) {
                    SSOLogIn(response.body().getToken());
                } else {
                    showToast(response.message());
                    metaOneSDKManager.logout();
                    reset();

                }
            }

            @Override
            public void onFailure(Call<SampleSSOLoginResponse> call, Throwable t) {
                showToast(t.getMessage());
                reset();
            }
        });
    }

    private void SSOLogIn(String token) {
        AuthApiModel.SSOLoginRequest ssoLoginRequest = new AuthApiModel.SSOLoginRequest(BuildConfig.SDK_REALM, token);
        metaOneSDKManager.login(ssoLoginRequest, this, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(AuthApiModel.AuthResponse response) {
                Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(ErrorResponse errorBody) {
                showToast(errorBody.getError());
                reset();
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
                reset();
            }
        });
    }

    private void isAuthorized() {
        boolean isAuthorized = metaOneSDKManager.getSessionActivityStatus() != SessionActivityStatus.UNAUTHORISED;
        if (isAuthorized) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAuthorized();
    }
}