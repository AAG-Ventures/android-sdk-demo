package com.metaone.metaone_sdk_demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.metaone.metaone_sdk_demo.components.base.BaseActivity;

import ventures.aag.metaonesdk.AppState;


public class SignCustomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_custom_tx);
        AppState.INSTANCE.setCurrentActivity(this);
        AppState.INSTANCE.setCurrentSource("internal");
        addButtonActions();
    }

    private void addButtonActions() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());

        Button signCustomTx = findViewById(R.id.sign_custom_tx);
        signCustomTx.setOnClickListener(v -> {
            Intent intent = new Intent(SignCustomActivity.this, SignCustomTransactionActivity.class);
            SignCustomActivity.this.startActivity(intent);
        });
        Button signCustomMessage = findViewById(R.id.sign_custom_message);
        signCustomMessage.setOnClickListener(v -> {
            Intent intent = new Intent(SignCustomActivity.this, SignCustomMessageActivity.class);
            SignCustomActivity.this.startActivity(intent);
        });
    }
}