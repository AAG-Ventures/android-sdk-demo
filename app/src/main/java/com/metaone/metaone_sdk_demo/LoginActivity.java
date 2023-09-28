package com.metaone.metaone_sdk_demo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private ProgressBar loadingIndicator;
    private Button loginButton;



    PreferenceUtils preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        TextView textView = findViewById(R.id.header_title);
        textView.setText("Login");
        preference = new PreferenceUtils(this);
        apiService = ApiClient.getClient();
        addButtonActions();
        setUpKeyboard();
    }

    protected void setUpKeyboard() {
        View layout = findViewById(R.id.root_layout);
        layout.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }
    private void setColors() {
        findViewById(R.id.root_layout).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.header_title)).setTextColor(colors.getBlack());
        TextInputEditText loginEmailInput=findViewById(R.id.login_email_input);
        loginEmailInput.setTextColor(colors.getBlack());
        loginEmailInput.setHintTextColor(colors.getBlack60());
    }

    private void addButtonActions() {
        setColors();
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> handleLogin());

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void reset() {
        setLoading(false);
        Button confirm2faButton = findViewById(R.id.confirm_2fa_button);
        confirm2faButton.setEnabled(false);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loginButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);
        } else {
            loginButton.setEnabled(true);
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private void handleLogin() {
        setLoading(true);
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
        metaOneSDKManager.login(token, this, new M1EnqueueCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                if(response){
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showToast("Login failed");
                    reset();
                }

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
        setColors();
    }
}