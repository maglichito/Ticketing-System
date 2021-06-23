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
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText name_register,surname_register,username_register, email_register, password_register,retype_password_register;
    Button back_button;
    Button register_button;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        statusBar();
        // Buttons
        name_register = findViewById(R.id.name_register);
        surname_register = findViewById(R.id.surname_register);
        username_register = findViewById(R.id.username_register);
        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        back_button = (Button) findViewById(R.id.back_button_reg);
        register_button = (Button) findViewById(R.id.register_button_reg);
        retype_password_register = findViewById(R.id.retype_password_register);
        bar = findViewById(R.id.progress_bar_register);
        bar.setVisibility(View.INVISIBLE);


        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LoginActivity();
            }
        });
        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerUser();
            }
        });

    }
    public void LoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    private void statusBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.pink,this.getTheme()));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.pink));
        }
    }

    public void registerUser(){
        API api = new API();
        String URL = api.getSignup();
        final String name = name_register.getText().toString().trim();
        final String surname = surname_register.getText().toString().trim();
        final String username = username_register.getText().toString().trim();
        final String email = email_register.getText().toString().trim();
        final String password = password_register.getText().toString().trim();
        final String retype_password = retype_password_register.getText().toString().trim();


        //first we will do the validations
        if (TextUtils.isEmpty(name)) {
            name_register.setError("This field is required.");
            name_register.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(surname)) {
            surname_register.setError("This field is required.");
            surname_register.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            username_register.setError("This field is required.");
            username_register.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            email_register.setError("This field is required.");
            email_register.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_register.setError("Enter valid e-mail.");
            email_register.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 5) {
            password_register.setError("Password must have at least 5 characters.");
            password_register.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || !retype_password.equalsIgnoreCase(password)) {
            retype_password_register.setError("Passwords do not match.");
            retype_password_register.requestFocus();
            return;
        }
        bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getJSONObject("response").getBoolean("error")) {

                                Toast.makeText(getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
                                //starting the login activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstname", name);
                params.put("lastname", surname);
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
