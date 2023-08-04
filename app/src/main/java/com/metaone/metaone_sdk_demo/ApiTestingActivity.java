package com.metaone.metaone_sdk_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.metaone.metaone_sdk_demo.components.base.BaseActivity;

import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.managers.MetaOneSDKApiManager;
import ventures.aag.metaonesdk.managers.MetaOneSDKManager;
import ventures.aag.metaonesdk.models.api.TransactionAPIModel;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;

public class ApiTestingActivity extends BaseActivity {
    MetaOneSDKManager metaOneSDKManager;

    MetaOneSDKApiManager apiManager;

    String[] options = {
            "GET:Wallets",
            "GET:Currencies",
            "GET:NFTs",
            "GET:Transactions"
    };

    String selectedItem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        metaOneSDKManager = new MetaOneSDKManager(this);
        setUpSelectValues();
    }

    protected void setValueToTextBox(String value) {
         TextView textView = findViewById(R.id.editTextTextMultiLine);
         textView.setText(value);
    }
    protected void setUpSelectValues() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.planets_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = options[position];
                if (selectedItem == "GET:Wallets") {
                    getWallets();
                }
                else if(selectedItem == "GET:Currencies") {
                    getCurrencies();
                }
                else if(selectedItem == "GET:NFTs") {
                    getNFTs();
                }
                else if(selectedItem == "GET:Transactions") {
                    getTransactions();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when no item is selected
            }
        });
    }

    protected void getWallets() {
        metaOneSDKManager.getApiManager().getWallets(new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserWalletsResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToTextBox(responseConverted);
            }
            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                // Handle error
            }
        });
    }

    protected void getCurrencies() {
        metaOneSDKManager.getApiManager().getCurrencies(new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserCurrenciesResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToTextBox(responseConverted);
            }
            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("Wallets", errorResponse.getError());
            }
        });
    }

    protected void getNFTs() {
        String walletId = null;
        String searchString = null;
        int limit = 100;
        int offset = 0;
        metaOneSDKManager.getApiManager().getNFTs(walletId, searchString, limit, offset,  new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserNFTsResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToTextBox(responseConverted);
            }
            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("Wallets", errorResponse.getError());
            }
        });
    }

    protected void getTransactions() {
        String walletId = null;
        String assetRef = null;
        String bip44 = null;
        String tokenAddress = null;
        int limit = 100;
        int offset = 0;
        metaOneSDKManager.getApiManager().getTransactions(walletId, assetRef, bip44, tokenAddress, limit, offset,  new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(TransactionAPIModel.TransactionsResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToTextBox(responseConverted);
            }
            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("Wallets", errorResponse.getError());
            }
        });
    }
}