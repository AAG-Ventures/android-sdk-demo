package com.metaone.metaone_sdk_demo;

import android.os.Bundle;
import android.widget.Button;

import com.metaone.metaone_sdk_demo.components.base.BaseActivity;

import ventures.aag.metaonesdk.helpers.Helpers.Companion.Themes;
import ventures.aag.metaonesdk.managers.MetaOneSDKUIManager;

public class ChangeThemeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
        addButtonActions();
    }


    private void onChangeTheme(int theme) {
        MetaOneSDKUIManager metaOneSDKUIManager = new MetaOneSDKUIManager();
        metaOneSDKUIManager.setCurrentTheme(theme);
        recreate();
    }

    private void addButtonActions() {

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());

        Button dark_button = findViewById(R.id.dark_button);
        dark_button.setOnClickListener(v -> {
            onChangeTheme(Themes.DARK.getId());

        });
        Button light_button = findViewById(R.id.light_button);
        light_button.setOnClickListener(v -> {
            onChangeTheme(Themes.LIGHT.getId());
        });
        Button blue_button = findViewById(R.id.blue_button);
        blue_button.setOnClickListener(v -> {
            onChangeTheme(Themes.BLUE.getId());
        });
    }

}
