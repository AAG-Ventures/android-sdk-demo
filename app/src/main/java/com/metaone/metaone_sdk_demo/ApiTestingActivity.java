package com.metaone.metaone_sdk_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.metaone.metaone_sdk_demo.components.base.BaseActivity;



import ventures.aag.metaonesdk.models.Contacts;
import ventures.aag.metaonesdk.models.ErrorResponse;
import ventures.aag.metaonesdk.models.M1EnqueueCallback;
import ventures.aag.metaonesdk.models.Response;
import ventures.aag.metaonesdk.models.api.ContactsApiModel;
import ventures.aag.metaonesdk.models.api.TransactionAPIModel;
import ventures.aag.metaonesdk.models.api.WalletsAPIModel;

public class ApiTestingActivity extends BaseActivity {
    String[] options = {
            "GET:Wallets",
            "GET:Currencies",
            "GET:NFTs",
            "GET:Transactions",
            "GET:UserContacts",
            "POST:AddUserContact",
            "PUT:UpdateUserContact",
            "DELETE:DeleteUserContact"
    };

    String selectedItem = "";
    String selectedId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        setUpSelectValues();
        setColors();
    }

    protected void setColors() {
        findViewById(R.id.root_layout).setBackgroundColor(colors.getBackground());

        ((TextView) findViewById(R.id.select_id)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.requestBodyLabel)).setTextColor(colors.getBlack());
        ((TextView) findViewById(R.id.responseLabel)).setTextColor(colors.getBlack());
        ((EditText) findViewById(R.id.requestBody)).setTextColor(colors.getBlack());
        ((EditText) findViewById(R.id.responseBody)).setTextColor(colors.getBlack());
    }


    protected void setValueToRequestTextBox(String value) {
        TextView textView = findViewById(R.id.requestBody);
        textView.setText(value);
    }

    protected void setValueToResponseTextBox(String value) {
        TextView textView = findViewById(R.id.responseBody);
        textView.setText(value);
    }

    protected void setUpSelectValues() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.planets_spinner);
        spinner.setBackgroundColor(colors.getBlack());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = options[position];
                LinearLayout requestBodyContainer = findViewById(R.id.requestBodyContainer);
                LinearLayout infoIdContainer = findViewById(R.id.infoIdContainer);
                requestBodyContainer.setVisibility(View.GONE);
                infoIdContainer.setVisibility(View.GONE);
                setValueToResponseTextBox("");
                if (selectedItem.contains("GET:")) {
                    if (selectedItem == "GET:Wallets") {
                        getWallets();
                    } else if (selectedItem == "GET:Currencies") {
                        getCurrencies();
                    } else if (selectedItem == "GET:NFTs") {
                        getNFTs();
                    } else if (selectedItem == "GET:Transactions") {
                        getTransactions();
                    } else if (selectedItem == "GET:UserContacts") {
                        getUserContacts(false);
                    }
                } else if (selectedItem.contains("POST:") || selectedItem.contains("PUT:")) {
                    requestBodyContainer.setVisibility(View.VISIBLE);
                    EditText requestBodyText = findViewById(R.id.requestBody);
                    requestBodyText.setText(getApiInitialBody(selectedItem));
                    if (selectedItem == "PUT:UpdateUserContact") {
                        infoIdContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    infoIdContainer.setVisibility(View.VISIBLE);
                }
                if (selectedItem == "PUT:UpdateUserContact" || selectedItem == "DELETE:DeleteUserContact") {
                    getUserContacts(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when no item is selected
            }
        });
    }

    public void onSubmitClick(View view) {
        EditText requestBodyText = findViewById(R.id.requestBody);
        String body = String.valueOf(requestBodyText.getText());

        if (selectedItem == "POST:AddUserContact") {
            Contacts.ContactRequest request = new Gson().fromJson(body, Contacts.ContactRequest.class);
            addUserContact(request);
        } else if (selectedItem == "PUT:UpdateUserContact") {
            Contacts.ContactRequest request = new Gson().fromJson(body, Contacts.ContactRequest.class);
            updateUserContact(selectedId, request);
        } else if (selectedItem == "DELETE:DeleteUserContact") {
            deleteUserContact(selectedId);
        }
    }

    protected void getWallets() {
        metaOneSDKManager.getApiManager().getWallets(new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserWalletsResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToResponseTextBox(responseConverted);
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
                setValueToResponseTextBox(responseConverted);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("Currencies", errorResponse.getError());
            }
        });
    }

    protected void getNFTs() {
        String walletId = null;
        String searchString = null;
        int limit = 100;
        int offset = 0;
        metaOneSDKManager.getApiManager().getNFTs(walletId, searchString, limit, offset, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(WalletsAPIModel.UserNFTsResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToResponseTextBox(responseConverted);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("NFTs", errorResponse.getError());
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
        metaOneSDKManager.getApiManager().getTransactions(walletId, assetRef, bip44, tokenAddress, limit, offset, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(TransactionAPIModel.TransactionsResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToResponseTextBox(responseConverted);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("Transactions", errorResponse.getError());
            }
        });
    }

    protected void setIdsSpinner(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.ids_spinner);
        spinner.setBackgroundColor(colors.getBlack());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedId = data[position];
                if (selectedItem == "PUT:UpdateUserContact" || selectedItem == "DELETE:DeleteUserContact") {
                    getUserContactWithId(selectedId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when no item is selected
            }
        });
    }

    protected void getUserContacts(boolean isSetRequest) {
        metaOneSDKManager.getApiManager().getUserContacts(new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(ContactsApiModel.ContactsResponse response) {
                if (isSetRequest) {
                    String[] ids = response.getData().getContacts().stream()
                            .map(Contacts.Contact::getId)
                            .toArray(String[]::new);
                    setIdsSpinner(ids);
                } else {
                    String responseConverted = new Gson().toJson(response);
                    setValueToResponseTextBox(responseConverted);
                }
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("Contacts", errorResponse.getError());
            }
        });
    }

    protected void getUserContactWithId(String id) {
        metaOneSDKManager.getApiManager().getUserContactWithId(id, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(ContactsApiModel.ContactResponse response) {
                String responseConverted = new Gson().toJson(new Contacts.ContactRequest(response.getData()));
                setValueToRequestTextBox(responseConverted);

            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
                Log.d("GetUserContactWithId", errorResponse.getError());
            }
        });
    }

    protected void addUserContact(Contacts.ContactRequest request) {
        metaOneSDKManager.getApiManager().addUserContact(request, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(ContactsApiModel.ContactResponse response) {
                String responseConverted = new Gson().toJson(response);
                setValueToResponseTextBox(responseConverted);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void updateUserContact(String id, Contacts.ContactRequest request) {
        metaOneSDKManager.getApiManager().updateUserContact(id, request, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(Response response) {
                String responseConverted = new Gson().toJson(response);
                setValueToResponseTextBox(responseConverted);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void deleteUserContact(String id) {
        metaOneSDKManager.getApiManager().deleteUserContact(id, new M1EnqueueCallback<>() {
            @Override
            public void onSuccess(Response response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getApiInitialBody(String api) {
        if (api == "POST:AddUserContact") {
            return "{\n" +
                    "    \"data\": {\n" +
                    "        \"name\": \"Melina\",\n" +
                    "        \"wallets\": [\n" +
                    "            {\n" +
                    "                \"name\": \"Melina's ETH wallet1\",\n" +
                    "                \"address\": \"0xc4827dad05333279874cd7d2c76f49b63ccb79033455\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "                \"name\": \"Melina's ETH wallet2\",\n" +
                    "                \"address\": \"0xc4827dad05333279874cd7d2c76f49b63ccb790323455\"\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    }\n" +
                    "}";
        }
        return "";
    }
}