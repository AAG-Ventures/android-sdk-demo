package com.metaone.metaone_sdk_demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.metaone.metaone_sdk_demo.components.base.BaseActivity;

import ventures.aag.metaonesdk.models.M1Color;

public class ChangeThemeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
        addButtonActions();
    }

   public  String lightTheme = "{\n" +
            "  \"alwaysWhite\": \"#FFFFFF\",\n" +
            "  \"alwaysBlack\": \"#101111\",\n" +
            "  \"primary\": \"#386CF3\",\n" +
            "  \"primary80\": \"#386CF3CC\",\n" +
            "  \"primary60\": \"#386CF399\",\n" +
            "  \"primary40\": \"#386CF366\",\n" +
            "  \"primary20\": \"#417FF6CC\",\n" +
            "  \"secondary\": \"#604EFF\",\n" +
            "  \"secondary80\": \"#604EFFCC\",\n" +
            "  \"secondary60\": \"#604EFF99\",\n" +
            "  \"secondary40\": \"#604EFF66\",\n" +
            "  \"secondary20\": \"#604EFF33\",\n" +
            "  \"secondary15\": \"#604EFF26\",\n" +
            "  \"primaryButtonBg\": \"#386CF3\",\n" +
            "  \"primaryButtonBgDisabled\": \"#386CF360\",\n" +
            "  \"primaryButtonText\": \"#FFFFFF\",\n" +
            "  \"secondaryButtonBg\": \"#417FF6CC\",\n" +
            "  \"secondaryButtonBgDisabled\": \"#417FF633\",\n" +
            "  \"secondaryButtonText\": \"#386CF3\",\n" +
            "  \"errorButtonBg\": \"#FFFFFF\",\n" +
            "  \"errorButtonText\": \"#D93F33\",\n" +
            "  \"green\": \"#1BAC3F\",\n" +
            "  \"greenBg\": \"#B7E8C3\",\n" +
            "  \"yellow\": \"#DEA511\",\n" +
            "  \"yellowBg\": \"#F0E29A\",\n" +
            "  \"yellow15\": \"#DEA51126\",\n" +
            "  \"red\": \"#D93F33\",\n" +
            "  \"redBg\": \"#F5B9B5\",\n" +
            "  \"blue\": \"#386CF3\",\n" +
            "  \"blueBg\": \"#C6DAFF\",\n" +
            "  \"wireframes\": \"#BDC2CA\",\n" +
            "  \"wireframesLight\": \"#D8E0E5\",\n" +
            "  \"gradientLight\": \"#E0F9FD\",\n" +
            "  \"gradientViolet\": \"#6851F5\",\n" +
            "  \"gradientBlue\": \"#7999FE\",\n" +
            "  \"average\": \"#F7931A\",\n" +
            "  \"background\": \"#F0F2F4\",\n" +
            "  \"background20\": \"#F0F2F433\",\n" +
            "  \"white\": \"#FFFFFF\",\n" +
            "  \"white20\": \"#FFFFFF33\",\n" +
            "  \"white50\": \"#FFFFFF80\",\n" +
            "  \"white80\": \"#FFFFFFCC\",\n" +
            "  \"black\": \"#101111\",\n" +
            "  \"black80\": \"#101111CC\",\n" +
            "  \"black60\": \"#10111199\",\n" +
            "  \"black40\": \"#10111166\",\n" +
            "  \"black20\": \"#10111133\",\n" +
            "  \"black15\": \"#10111126\",\n" +
            "  \"black10\": \"#1011111A\",\n" +
            "  \"black5\": \"#1011110D\",\n" +
            "  \"pin\": \"#0066FF\"\n" +
            "}";

    public String darkTheme = "{" +
            "  \"background\": \"#18191E\",\n" +
            "  \"background20\": \"#18191E33\",\n" +
            "  \"white\": \"#34353C\",\n" +
            "  \"white20\": \"#34353C33\",\n" +
            "  \"white50\": \"#34353C80\",\n" +
            "  \"white80\": \"#34353CCC\",\n" +
            "  \"black\": \"#FFFFFF\",\n" +
            "  \"black80\": \"#FFFFFFCC\",\n" +
            "  \"black60\": \"#FFFFFF99\",\n" +
            "  \"black40\": \"#FFFFFF66\",\n" +
            "  \"black20\": \"#FFFFFF33\",\n" +
            "  \"black15\": \"#FFFFFF26\",\n" +
            "  \"black10\": \"#FFFFFF1A\",\n" +
            "  \"black5\": \"#FFFFFF0D\",\n" +
            "  \"pin\":  \"#8B7DFF\"\n" +
            "}";

    private void onChangeTheme(String colorsString) {
        M1Color.ColorsScheme colors = new Gson().fromJson(colorsString, M1Color.ColorsScheme.class);
        metaOneSDKUIManager.setColorsScheme(colors);
        recreate();
    }

    private void addButtonActions() {
        findViewById(R.id.background).setBackgroundColor(colors.getBackground());
        ((TextView) findViewById(R.id.title)).setTextColor(colors.getBlack());
        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> onBackPressed());

        Button dark_button = findViewById(R.id.dark_button);
        dark_button.setOnClickListener(v -> {
            onChangeTheme(darkTheme);

        });
        Button light_button = findViewById(R.id.light_button);
        light_button.setOnClickListener(v -> {
            onChangeTheme(lightTheme);
        });
    }

}
