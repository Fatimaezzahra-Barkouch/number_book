package com.example.mynumber;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class List_Contact extends AppCompatActivity {
    RecyclerView recyclerView;
    myAdapter myadapter;
    List<Contact> contacts;
    Contact contact;
    LinearLayoutManager linearLayoutManager;
    String type, aRechercher, country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);
        getSupportActionBar().hide();
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewContact);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        contacts = new ArrayList<>();
        myadapter = new myAdapter(contacts, this);
        recyclerView.setAdapter(myadapter);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        aRechercher = intent.getStringExtra("aRechercher");
        country = intent.getStringExtra("country");
        getToRecycle();
    }

    private String returnUrl(String type) {
        if (type.equalsIgnoreCase("nom")) {
            return "https://convincing-sciences.000webhostapp.com/selectByNom.php";
        } else if (type.equalsIgnoreCase("numero")) {
            return "https://convincing-sciences.000webhostapp.com/selectByNumero.php";
        }
        return null;
    }


    private void getToRecycle() {
        Log.d("a",  "a");
        StringRequest request = new StringRequest(Request.Method.POST, returnUrl(type),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("b",  "b");

                        try {
                            Log.d("c",  "c");

                            JSONObject jsonObject = new JSONObject(response);
                            String succes = jsonObject.getString("success");
                            Log.d("succes",  succes);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (succes.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String nom = object.getString("nom");
                                    String numero = object.getString("numero");
                                    String country = object.getString("country");
                                    Log.d("id",  id);
                                    Log.d("nom",  nom);
                                    Log.d("numero",  numero);
                                    Log.d("country",  country);
                                    Contact contact = new Contact(id, nom, numero, country);
                                    contacts.add(contact);
                                    myadapter.notifyDataSetChanged();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(List_Contact.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("param", aRechercher);
                param.put("country", country);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


}