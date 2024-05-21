package com.metaone.metaone_sdk_demo;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.metaone.metaone_sdk_demo.components.base.BaseActivity;
import com.metaone.metaone_sdk_demo.utils.Drawable;

import java.util.Optional;

import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.MetaOneSDKApiManager;
import ventures.aag.metaonesdk.models.Wallets;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;

public class SignCustomMessageActivity extends BaseActivity {
    MetaOneSDKApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_message);
        apiManager = metaOneSDKManager.getApiManager();
        addButtonActions();
    }

    private void addButtonActions() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.textViewHeader)).setTextColor(colors.getBlack());
        GradientDrawable border = new Drawable().createBorderDrawable(colors.getBlack());
        TextView textViewMessage = findViewById(R.id.textViewMessage);
        textViewMessage.setBackground(border);
        textViewMessage.setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.textViewHexHeader)).setTextColor(colors.getBlack());
        TextView textViewHex = findViewById(R.id.textViewHex);
        textViewHex.setTextColor(colors.getBlack());
        textViewHex.setBackground(border);

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());

        Button signCustomMessage = findViewById(R.id.sign_custom_message);
        signCustomMessage.setOnClickListener(v -> {
            apiManager.getWallets(new M1EnqueueCallback<>() {
                @Override
                public void onSuccess(WalletsAPIModel.UserWalletsResponse response) {
                    Optional<Wallets.UserWallet> ethWallet = response.getWallets().stream().filter(wallet -> "ETH".equalsIgnoreCase(wallet.getCurrencySymbol())).findFirst();
                    String jsonString = "{\n" +
                            "    \"id\": 1687977882197,\n" +
                            "    \"type\": \"signMessage\",\n" +
                            "    \"object\": {\n" +
                            "        \"data\": \"0x5468697320697320612074657374206d657373616765\"\n" +
                            "    },\n" +
                            "    \"externalWalletId\": " + ethWallet.get().getExternalWalletId() + ",\n" +
                            "    \"url\": \"https://etherscan.io/verifiedSignatures#\"\n" +
                            "}";
                    metaOneSDKManager.createSigning(jsonString);

                }

                @Override
                public void onError(ErrorResponse errorResponse) {
                    Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
