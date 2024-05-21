package com.metaone.metaone_sdk_demo;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.metaone.metaone_sdk_demo.components.base.BaseActivity;
import com.metaone.metaone_sdk_demo.utils.Drawable;

import java.util.ArrayList;
import java.util.List;

import ventures.aag.metaonesdk.models.Contacts;
import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.MetaOneSDKApiManager;
import ventures.aag.metaonesdk.models.Signing;
import ventures.aag.metaonesdk.models.Wallets;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;

public class SignCustomTransactionActivity extends BaseActivity {

    MetaOneSDKApiManager apiManager;
    Wallets.UserWallet wallet;
    EditText signingData;

    EditText responseData;

    Button goBack, signCustomTransaction;
    ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_custom_tx);
        apiManager = metaOneSDKManager.getApiManager();
        initView();
        updateUiColors();
        addButtonActions();
        fetchWallets();
    }

    private void initView() {
        goBack = findViewById(R.id.go_back);
        signingData = findViewById(R.id.signing_data);
        responseData = findViewById(R.id.responseBody);
        signCustomTransaction = findViewById(R.id.sign_custom_transaction);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        TextWatcher watcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() != null) checkParams();
            }
        };
        signingData.addTextChangedListener(watcher);
    }

    private void updateUiColors() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.select_wallet_label)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.signing_data_header)).setTextColor(colors.getBlack());
        GradientDrawable border = new Drawable().createBorderDrawable(colors.getBlack());


        signingData.setTextColor(colors.getBlack());
        signingData.setBackground(border);
    }

    private void fetchWallets() {
        apiManager.getWallets(new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserWalletsResponse response) {
                List<Wallets.UserWallet> wallets = response.getWallets();
                if (wallets.size() > 0) {
                    RelativeLayout loader = findViewById(R.id.loadingOverlay);
                    loader.setVisibility(View.GONE);
                    setWalletsSpinner(wallets);
                } else {
                    ProgressBar loader = findViewById(R.id.loader);
                    loader.setIndeterminateTintList(ColorStateList.valueOf(colors.getPrimary()));
                    loader.setVisibility(View.INVISIBLE);
                    goBack.setVisibility(View.VISIBLE);
                    goBack.setOnClickListener(v -> onBackPressed());
                }
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addButtonActions() {

        goBack.setOnClickListener(v -> onBackPressed());
        signCustomTransaction.setOnClickListener(v -> {
            onClickSignButton();
        });
    }

    private void checkParams() {
        String tx = String.valueOf(signingData.getText());
        if (wallet != null && !tx.isEmpty() && tx.length()>0) {
            signCustomTransaction.setEnabled(true);
        } else {
            signCustomTransaction.setEnabled(false);
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            signCustomTransaction.setEnabled(false);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            signCustomTransaction.setEnabled(true);
        }
    }

    private void onClickSignButton() {
        String tx = String.valueOf(signingData.getText());
        setLoading(true);
        responseData.setText("");
        metaOneSDKManager.signCustomTransaction(wallet, tx, Signing.TransactionSpeed.MEDIUM,
                new M1EnqueueCallback<>() {
                    @Override
                    public void onSuccess(Signing.DispatchedRpcEvent response) {
                        setLoading(false);
                        Toast.makeText(getApplicationContext(), response != null ? "Transaction successfully signed and sent" : "Transaction failed", Toast.LENGTH_LONG).show();
                        String responseConverted = new Gson().toJson(response);
                        responseData.setText(responseConverted);
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        setLoading(false);
                        responseData.setText(errorResponse.getError());
                        Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    protected void setWalletsSpinner(List<Wallets.UserWallet> wallets) {
        List<String> walletIds = new ArrayList<>();
        for (Wallets.UserWallet wallet : wallets) {
            walletIds.add(wallet.getName() + " " + wallet.getAddress());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, walletIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.wallets_spinner);
        spinner.setBackgroundColor(colors.getBlack20());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wallet = wallets.get(position);
                checkParams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when no item is selected
            }
        });
    }

}
