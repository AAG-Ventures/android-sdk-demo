package com.metaone.metaone_sdk_demo;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import com.metaone.metaone_sdk_demo.components.base.BaseActivity;

import ventures.aag.metaonesdk.helpers.Helpers.Companion.Languages;

public class ChangeLanguageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        addButtonActions();
    }


    private void onChangeLanguage(String locale) {
        metaOneSDKUIManager.setCurrentLanguage(locale);
        recreate();
    }

    private void addButtonActions() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());

        Button en_button = findViewById(R.id.en_button);
        en_button.setOnClickListener(v -> {
            onChangeLanguage(Languages.ENGLISH.getValue());

        });
        Button in_button = findViewById(R.id.in_button);
        in_button.setOnClickListener(v -> {
            onChangeLanguage(Languages.INDONESIAN.getValue());
        });
        Button fil_button = findViewById(R.id.fil_button);
        fil_button.setOnClickListener(v -> {
            onChangeLanguage(Languages.FILIPINO.getValue());
        });
        Button vi_button = findViewById(R.id.vi_button);
        vi_button.setOnClickListener(v -> {
            onChangeLanguage(Languages.VIETNAMESE.getValue());
        });
        Button zh_cn_button = findViewById(R.id.zh_cn_button);
        zh_cn_button.setOnClickListener(v -> {
            onChangeLanguage(Languages.CHINESE_SIMPLIFIED.getValue());
        });
        Button zh_tw_button = findViewById(R.id.zh_tw_button);
        zh_tw_button.setOnClickListener(v -> {
            onChangeLanguage(Languages.CHINESE_TRADITIONAL.getValue());
        });
    }

}
