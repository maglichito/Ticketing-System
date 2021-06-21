package com.example.ticketing_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ticketing_system.data.API;
import com.example.ticketing_system.data.SharedPrefManager;
import com.example.ticketing_system.data.User;
import com.example.ticketing_system.data.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    EditText username_login, password_login;
    API api = new API();
    Button register_button;
    Button login_button;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        statusBar();
        bar = findViewById(R.id.progress_bar_login);
        bar.setVisibility(View.INVISIBLE);
        password_login = findViewById(R.id.password_login);
        username_login = findViewById(R.id.username_login);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        register_button = (Button) findViewById(R.id.register_button);
        login_button = (Button) findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                userLogin();
            }
        });

        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RegisterActivity();
            }
        });
    }
    public void RegisterActivity(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void statusBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.black,this.getTheme()));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    private void userLogin() {

        //URL
        String URL = api.getLogin();
        //first getting the values
        final String username = username_login.getText().toString();
        final String password = password_login.getText().toString();
        //validating inputs
        if (TextUtils.isEmpty(username)) {
            username_login.setError("Unesite username!");
            username_login.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            password_login.setError("Unesite lozinku!");
            password_login.requestFocus();
            return;
        }
        bar.setVisibility(View.VISIBLE);
        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // progressBar.setVisibility(View.GONE);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getJSONObject("response").getBoolean("error") == false) {

                                Toast.makeText(getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("username"),
                                        userJson.getString("email"),
                                        userJson.getString("firstname"),
                                        userJson.getString("lastname")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
                                bar.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}