package com.example.mynumber;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText editTextInput;
    private Button recherche;
    private String type;
    private Cursor cursor;
    private ArrayList hashMapsArrayList;
    private static final String url = "https://convincing-sciences.000webhostapp.com/ajouterNumberBook.php";

    private CountryCodePicker ccp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        radioGroup = findViewById(R.id.radioGroup);
        editTextInput = findViewById(R.id.editTextInput);
        recherche = findViewById(R.id.buttonRecherche);
        ccp = findViewById(R.id.ccp);
        getContactList();

        radioButton = findViewById(R.id.radioButtonNumero);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ccp.setVisibility(view.VISIBLE);
            }

            ;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });



        recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                if (radioId == -1) {
                    Toast.makeText(getApplicationContext(), "Veuillez choisir le type de " +
                                    "recherche!",
                            Toast.LENGTH_LONG).show();
                } else {
                    radioButton = findViewById(radioId);
                    type = radioButton.getText().toString();
                    String contactACherche = editTextInput.getText().toString();
                    String code = ccp.getSelectedCountryCode();
                    String country = ccp.getSelectedCountryNameCode();

                    if (contactACherche.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Activity currentActivity = (Activity) view.getContext();
                        Intent i = new Intent(currentActivity, List_Contact.class);
                        i.putExtra("type", type);
                        i.putExtra("country", country);
                        i.putExtra("aRechercher", contactACherche);
                        currentActivity.startActivity(i);
                        closeKeyboard();
                    }
                }
            }
        });

    }


    @SuppressLint("Range")
    private void getContactList() {

        if (cursor != null) {
            cursor.moveToFirst();
        }
        try {

            cursor = getApplicationContext().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            int Idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
            cursor.moveToFirst();


            Set<String> ids = new HashSet<>();
            do {
                String contactid = cursor.getString(Idx);
                if (!ids.contains(contactid)) {
                    ids.add(contactid);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    String name = cursor.getString(nameIdx);
                    String phoneNumber = cursor.getString(phoneNumberIdx);
                    String countryOfNumber = getCountryOfNumber(phoneNumber);
                    insert(name, phoneNumber, countryOfNumber);
                    if (!phoneNumber.contains("*")) {
                        hashMap.put("contactid", "" + contactid);
                        hashMap.put("name", "" + name);
                        hashMap.put("phoneNumber", "" + phoneNumber);
                        hashMap.put("country", "" + countryOfNumber);
                        if (hashMapsArrayList != null) {
                            hashMapsArrayList.add(hashMap);
                        }
                    }
                }

            } while (cursor.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getCountryOfNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getApplicationContext());
        try {
            Phonenumber.PhoneNumber NumberProto = phoneUtil.parse(phoneNumber, null);
            if (!phoneUtil.isValidNumber(NumberProto)) {
                return "";
            }
            String regionISO = phoneUtil.getRegionCodeForCountryCode(NumberProto.getCountryCode());
            return regionISO;
        } catch (NumberParseException e) {
            return "";
        }
    }

    private void insert(String name, String phoneNo, String countryOfNumber) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "inserer dans la BD",
                        Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_LONG).show();

            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("nom", name);
                param.put("numero", phoneNo);
                param.put("country", countryOfNumber);
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}