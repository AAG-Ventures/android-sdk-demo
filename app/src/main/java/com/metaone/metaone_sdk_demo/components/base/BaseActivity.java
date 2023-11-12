package com.metaone.metaone_sdk_demo.components.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Locale;

import ventures.aag.metaonesdk.managers.MetaOneSDKManager;
import ventures.aag.metaonesdk.managers.MetaOneSDKUIManager;
import ventures.aag.metaonesdk.models.M1Color;


abstract public class BaseActivity extends AppCompatActivity {

    public MetaOneSDKUIManager metaOneSDKUIManager;
    public MetaOneSDKManager metaOneSDKManager;
    public M1Color.IntColorsScheme colors;
    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metaOneSDKManager =  new MetaOneSDKManager(this);
        metaOneSDKUIManager =  metaOneSDKManager.getUiManager();
        colors = metaOneSDKUIManager.getColorsScheme().toIntColors();
    }

    private void clearActivityReferences() {
        if (metaOneSDKUIManager.getCurrentActivity() == this)
            metaOneSDKUIManager.setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        metaOneSDKUIManager.setCurrentActivity(this);
        colors = metaOneSDKUIManager.getColorsScheme().toIntColors();
        setCustomTheme();
    }

    @Override
    protected void onPause() {
        clearActivityReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearActivityReferences();
        super.onDestroy();
    }

    private void setCustomTheme() {
        getWindow().setStatusBarColor(colors.getBackground());
    }
}