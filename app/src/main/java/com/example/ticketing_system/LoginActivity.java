package com.example.ticketing_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.ticketing_system.data.Network;
import com.example.ticketing_system.data.SharedPrefManager;
import com.example.ticketing_system.data.User;
import com.example.ticketing_system.data.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private EditText username_login, password_login;
    private API api = new API();
    private Button register_button;
    private Button login_button;
    private ProgressBar bar;
    private Network network = new Network(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        statusBar();

        bar = findViewById(R.id.progress_bar_login);
        bar.setVisibility(View.INVISIBLE);
        password_login = findViewById(R.id.password_login);
        username_login = findViewById(R.id.username_login);

        // If user is logged in redirect to MainActivity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        register_button = (Button) findViewById(R.id.register_button);
        login_button = (Button) findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // If device connected to network then attempt login
                if(network.isNetworkAvailable()){
                    userLogin();
                }else{
                    Toast.makeText(getApplicationContext(), "You are not connected to a network.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RegisterActivity();
            }
        });
    }

    // Start RegisterActivity
    public void RegisterActivity(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }


    // Setting status bar to pink to match theme
    private void statusBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.pink,this.getTheme()));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.pink));
        }
    }

    // this method will log user in
    private void userLogin() {

        //URL
        String URL = api.getLogin();
        //first getting the values
        final String username = username_login.getText().toString();
        final String password = password_login.getText().toString();
        //validating inputs
        if (TextUtils.isEmpty(username)) {
            username_login.setError("This field is required!");
            username_login.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            password_login.setError("This field is required!");
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
                            if (!obj.getJSONObject("response").getBoolean("error")) {

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
                                bar.setVisibility(View.INVISIBLE);
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

                        if(network.checkConnectivity()){
                            Toast.makeText(getApplicationContext(), "There is problem with server, we apologize for this inconvenience.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Connection timed out, check your network.", Toast.LENGTH_SHORT).show();
                        }
                        bar.setVisibility(View.INVISIBLE);
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