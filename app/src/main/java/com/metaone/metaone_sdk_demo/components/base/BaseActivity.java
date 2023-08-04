package com.metaone.metaone_sdk_demo.components.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ventures.aag.metaonesdk.managers.MetaOneSDKUIManager;


abstract public class BaseActivity extends AppCompatActivity {

    private MetaOneSDKUIManager metaOneSDKUIManager;
    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metaOneSDKUIManager = new MetaOneSDKUIManager();
        setLocale();
        theme = metaOneSDKUIManager.getCurrentTheme();
        setTheme(theme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (theme != metaOneSDKUIManager.getCurrentTheme()) {
            theme = metaOneSDKUIManager.getCurrentTheme();
            recreate();
        }
    }

    private void setLocale() {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = metaOneSDKUIManager.getCurrentLanguage();
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}