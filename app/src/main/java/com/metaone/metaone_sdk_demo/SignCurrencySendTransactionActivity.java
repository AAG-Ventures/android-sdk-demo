package com.metaone.metaone_sdk_demo;

import android.content.res.ColorStateList;
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

import java.util.ArrayList;
import java.util.List;

import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.MetaOneSDKApiManager;
import ventures.aag.metaonesdk.models.Wallets;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;

public class SignCurrencySendTransactionActivity extends BaseActivity {

    MetaOneSDKApiManager apiManager;

    Spinner assetsSpinner, walletsSpinner;
    Wallets.UserWallet wallet;
    Wallets.WalletToken token;
    EditText personalNote, toAddress, amountInput;
    Button goBack, signCustomTransaction;

    ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_currency_send_tx);
        apiManager = metaOneSDKManager.getApiManager();
        initView();
        updateUiColors();
        addButtonActions();
        fetchWallets();
    }

    private void initView() {
        goBack = findViewById(R.id.go_back);
        walletsSpinner = findViewById(R.id.wallets_spinner);
        assetsSpinner = findViewById(R.id.assets_spinner);
        personalNote = findViewById(R.id.personal_note);
        toAddress = findViewById(R.id.to_address);
        amountInput = findViewById(R.id.amount);
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
        amountInput.addTextChangedListener(watcher);
        toAddress.addTextChangedListener(watcher);
    }

    private void updateUiColors() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.select_wallet_label)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.select_asset_label)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.personal_note_header)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.to_address_header)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.amount_header)).setTextColor(colors.getBlack());
        walletsSpinner.setBackgroundColor(colors.getAlwaysWhite());
        assetsSpinner.setBackgroundColor(colors.getAlwaysWhite());
        personalNote.setBackgroundColor(colors.getAlwaysWhite());
        toAddress.setBackgroundColor(colors.getAlwaysWhite());
        amountInput.setBackgroundColor(colors.getAlwaysWhite());
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
        String amount = String.valueOf(amountInput.getText());
        String address = String.valueOf(toAddress.getText());
        if (wallet != null && amount != null && address != null) {
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
        String amount = String.valueOf(amountInput.getText());
        String address = String.valueOf(toAddress.getText());
        String note = String.valueOf(personalNote.getText());
        setLoading(true);
        metaOneSDKManager.sendTransaction(wallet, address, amount, note, token, "",
                new M1EnqueueCallback<>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        setLoading(false);
                        Toast.makeText(getApplicationContext(),response?  "Transaction successfully signed and sent" : "Transaction failed", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        setLoading(false);
                        Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void setWalletsSpinner(List<Wallets.UserWallet> wallets) {
        List<String> walletIds = new ArrayList<>();
        for (Wallets.UserWallet wallet : wallets) {
            walletIds.add(wallet.getName() + " " + parseBalance(wallet.getBalance()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, walletIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        walletsSpinner.setAdapter(adapter);
        walletsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wallet = wallets.get(position);
                setAssetsSpinner();
                checkParams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when no item is selected
            }
        });
    }

    private String parseBalance(String value) {
        return value.substring(0, Math.min(8, value.length()));
    }

    private void setAssetsSpinner() {
        token = null;
        List<String> assetIds = new ArrayList<>();
        assetIds.add(wallet.getCurrencyName() + " " + parseBalance(wallet.getBalance()));
        for (Wallets.WalletToken token : wallet.getTokens()) {
            assetIds.add(token.getCurrencyName() + " " + parseBalance(token.getBalance()));
        }
        if (assetIds.stream().count() == 1) {
            ((TextView) findViewById(R.id.select_asset_label)).setVisibility(View.GONE);
            assetsSpinner.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, assetIds);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetsSpinner.setAdapter(adapter);
            assetsSpinner.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.select_asset_label)).setVisibility(View.VISIBLE);
            assetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        token = wallet.getTokens().get(position - 1);
                    } else {
                        token = null;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle when no item is selected
                }
            });
        }

    }

}
