package com.metaone.metaone_sdk_demo;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.metaone.metaone_sdk_demo.components.base.BaseActivity;
import com.metaone.metaone_sdk_demo.utils.Drawable;

import java.util.ArrayList;
import java.util.List;

import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.MetaOneSDKApiManager;
import ventures.aag.metaonesdk.managers.MetaOneSDKManager;
import ventures.aag.metaonesdk.models.Wallets;
import ventures.aag.metaonesdk.models.api.SigningApiModel;
import ventures.aag.metaonesdk.models.api.TransactionAPIModel;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;

public class SignCustomTransactionActivity extends BaseActivity {

    MetaOneSDKApiManager apiManager;
    Wallets.UserWallet wallet;
    String gasAmount;
    String gasLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_transaction);
        apiManager = metaOneSDKManager.getApiManager();
        addButtonActions();
        setValues();
    }

    private void setValues() {
        apiManager.getWallets(new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserWalletsResponse response) {
                List<Wallets.UserWallet> wallets = filterTestnetBitcoinWallets(response.getWallets());
                if (wallets.size() > 0) {
                    RelativeLayout loader = findViewById(R.id.loadingOverlay);
                    TextView fromAddress = findViewById(R.id.from_address);
                    wallet = wallets.get(0);
                    fromAddress.setText(wallet.getAddress());
                    loader.setVisibility(View.GONE);
                } else {
                    ProgressBar loader = findViewById(R.id.loader);
                    loader.setIndeterminateTintList(ColorStateList.valueOf(colors.getPrimary()));
                    TextView hint = findViewById(R.id.no_wallets);
                    hint.setTextColor(colors.getBlack());
                    Button goBack = findViewById(R.id.overlay_go_back);
                    loader.setVisibility(View.INVISIBLE);
                    hint.setVisibility(View.VISIBLE);
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
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.to_address_header)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.btc_amount)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.detail)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.gasPriceLabel)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.gasLimitLabel)).setTextColor(colors.getBlack());

        GradientDrawable border = new Drawable().createBorderDrawable(colors.getBlack());

        TextView to_address = findViewById(R.id.to_address);
        to_address.setTextColor(colors.getBlack());
        to_address.setBackground(border);

        ((TextView) findViewById(R.id.from_address_header)).setTextColor(colors.getBlack());

        TextView from_address = findViewById(R.id.from_address);
        from_address.setTextColor(colors.getBlack());
        from_address.setBackground(border);

        TextView gasPrice = findViewById(R.id.gasPrice);
        gasPrice.setTextColor(colors.getBlack());
        gasPrice.setBackground(border);

        TextView gasLimit = findViewById(R.id.gasLimit);
        gasLimit.setTextColor(colors.getBlack());
        gasLimit.setBackground(border);

        TextView amount = findViewById(R.id.amount);
        amount.setTextColor(colors.getBlack());
        amount.setBackground(border);

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());

        Button calculate = findViewById(R.id.calculate_fees);
        calculate.setOnClickListener(v -> calculateFees());

        Button signCustomTransaction = findViewById(R.id.sign_custom_transaction);
        signCustomTransaction.setOnClickListener(v -> {
            String jsonString = "{\"type\":\"currencyTransactionSign\",\"data\":{\"walletId\":\"" + wallet.get_id() + "\",\"senderAddress\":\"" + wallet.getAddress() + "\",\"parentWalletAddress\":\"" + wallet.getAddress() + "\",\"externalWalletId\":\"" + wallet.getExternalWalletId() + "\",\"parentWalletExternalId\":\"" + wallet.getExternalWalletId() + "\",\"bip44\":\"" + wallet.getExternalWalletId() + "\",\"currency\":\"bitcoin\",\"receiverAddress\":\"2NFdZe8jeVxXn3cJBMe8yuU3FBkmHQN2mf2\",\"amount\":\"0.00001\"},\"label\":\"Confirm transaction\",\"description\":\"\",\"gasPrice\":\"0.00001\",\"nonce\":0}";
            metaOneSDKManager.createSigning(jsonString);
        });
    }

    private List<Wallets.UserWallet> filterTestnetBitcoinWallets(List<Wallets.UserWallet> wallets) {
        List<Wallets.UserWallet> filteredWallets = new ArrayList<>();
        for (Wallets.UserWallet wallet : wallets) {
            if (isBitcoinTestnetWallet(wallet) && hasNonZeroBalance(wallet) && !wallet.getAddress().equals("2NFdZe8jeVxXn3cJBMe8yuU3FBkmHQN2mf2")) {
                filteredWallets.add(wallet);
            }
        }
        return filteredWallets;
    }

    private void calculateFees() {
        apiManager.estimateGasPrice("BTC-TEST", new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(SigningApiModel.GasPriceResponse resp) {
                runOnUiThread(() -> {
                    TextView gasPrice = findViewById(R.id.gasPrice);
                    gasAmount = resp.getData().getLow().getAmount();
                    gasPrice.setText(gasAmount);
                    Button send = findViewById(R.id.sign_custom_transaction);
                    send.setEnabled(true);
                });
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
            }
        });
        SigningApiModel.GasLimitRequest gasLimitRequest = new SigningApiModel.GasLimitRequest("BTC-TEST", wallet.getAddress(), "2NFdZe8jeV×Xn3cJBMe8yuU3FBkmHQN2mf2", "[\"2NFdZe8jeV×Xn3cJBMe8yuU3FBkmHQN2mf2\", \"1\"]", null, TransactionAPIModel.TxDataTypes.COIN, "0.0001");
        apiManager.estimateGasLimit(gasLimitRequest, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(SigningApiModel.GasLimitResponse response) {
                TextView gasPrice = findViewById(R.id.gasLimit);
                Log.v("AMOUNT", response.getGasLimit());
                gasLimit = response.getGasLimit();
                gasPrice.setText(gasLimit);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isBitcoinTestnetWallet(Wallets.UserWallet wallet) {
        if (wallet.getNetwork() != null && wallet.getNetwork().getChainId() != null) {
            return wallet.getNetwork().getChainId().equals("BTC-TEST");
        }
        return false;
    }

    private boolean hasNonZeroBalance(Wallets.UserWallet wallet) {
        String balance = wallet.getBalance();
        return balance != null && !balance.isEmpty() && !balance.equals("0");
    }
}
