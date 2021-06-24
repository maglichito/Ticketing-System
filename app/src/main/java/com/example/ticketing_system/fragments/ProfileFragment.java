package com.example.ticketing_system.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ticketing_system.LoginActivity;
import com.example.ticketing_system.MainActivity;
import com.example.ticketing_system.R;
import com.example.ticketing_system.data.API;
import com.example.ticketing_system.data.Network;
import com.example.ticketing_system.data.SharedPrefManager;
import com.example.ticketing_system.data.User;
import com.example.ticketing_system.data.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment{

    private ProgressBar bar,bar_change;
    private EditText oldpassword,newpassword,name,lastname,username,email;
    private User u;
    private API api = new API();
    private Button changeButton,logoutButton,password_change,passwordChange_dialog;
    private Dialog myDialog;
    private Network network;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_profile,container,false);

        name = v.findViewById(R.id.name_profile);
        lastname = v.findViewById(R.id.lastname_profile);
        username = v.findViewById(R.id.username_profile);
        email = v.findViewById(R.id.email_profile);
        bar = v.findViewById(R.id.progress_bar_profile);
        bar.setVisibility(View.INVISIBLE);
        u = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        name.setText(u.getName());
        lastname.setText(u.getLastname());
        username.setText(u.getUsername());
        email.setText(u.getEmail());
        changeButton = v.findViewById(R.id.change_button);
        logoutButton = v.findViewById(R.id.logout_profile);
        network = new Network(getActivity());

        changeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(network.isNetworkAvailable()){
                    changeInfo();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "You are not connected to a network.", Toast.LENGTH_LONG).show();
                }
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).logout();
            }
        });


        // Opening dialog
        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.custompopup);
        bar_change = myDialog.findViewById(R.id.progress_bar_change);
        password_change = v.findViewById(R.id.password_change_button);
        oldpassword = myDialog.findViewById(R.id.old_password);
        newpassword = myDialog.findViewById(R.id.new_password);
        password_change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                bar_change.setVisibility(View.INVISIBLE);
                myDialog.show();
            }
        });

        // Set on dialog button a changePassword() click event
        passwordChange_dialog = myDialog.findViewById(R.id.passwordChange_dialog);
        passwordChange_dialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(network.isNetworkAvailable()){
                    changePassword();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "You are not connected to a network.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    // this method will change user information
    private void changeInfo(){
        //URL
        String URL = api.getProf_change();
        //first getting the values
        final String id =  Integer.toString(u.getId());
        final String first_name = name.getText().toString();
        final String last_name = lastname.getText().toString();
        final String user_name = username.getText().toString();
        final String e_mail = email.getText().toString();
        //first we will do the validations
        if (TextUtils.isEmpty(first_name)) {
            name.setError("This field is required.");
            name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(last_name)) {
            lastname.setError("This field is required.");
            lastname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(user_name)) {
            username.setError("This field is required.");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(e_mail)) {
            email.setError("This field is required.");
            email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()) {
            email.setError("Enter valid e-mail.");
            email.requestFocus();
            return;
        }

        // If no changes are made do not send request
        if(SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser().getName().equals(first_name) &&
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser().getLastname().equals(last_name) &&
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser().getUsername().equals(user_name) &&
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser().getEmail().equals(e_mail)){
            Toast.makeText(getActivity().getApplicationContext(), "Make changes before submitting.", Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(getActivity().getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
                                SharedPrefManager.getInstance(getActivity().getApplicationContext()).clear();

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
                                SharedPrefManager.getInstance(getActivity().getApplicationContext()).userLogin(user);
                                //starting the profile activity
                                bar.setVisibility(View.INVISIBLE);
                                myDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity().getApplicationContext(), "There is problem with server, we apologize for this inconvenience.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Connection timed out, check your network.", Toast.LENGTH_LONG).show();
                        }
                        bar.setVisibility(View.INVISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("firstname", first_name);
                params.put("lastname", last_name);
                params.put("username", user_name);
                params.put("email", e_mail);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    // this method will change user password
    private void changePassword() {

        //URL
        String URL = api.getPass_change();
        //first getting the values
        final String id =  Integer.toString(u.getId());
        final String old_password = oldpassword.getText().toString();
        final String new_password = newpassword.getText().toString();
        //validating inputs
        if (TextUtils.isEmpty(old_password) || new_password.length() < 5) {
            oldpassword.setError("Password must have at least 5 characters.");
            oldpassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(new_password) || new_password.length() < 5) {
            newpassword.setError("Password must have at least 5 characters.");
            newpassword.requestFocus();
            return;
        }
        if (old_password.equals(new_password)) {
            newpassword.setError("Passwords are same.");
            newpassword.requestFocus();
            return;
        }
        bar_change.setVisibility(View.VISIBLE);
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

                                Toast.makeText(getActivity().getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
                                bar_change.setVisibility(View.INVISIBLE);
                                myDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
                                bar_change.setVisibility(View.INVISIBLE);
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
                            Toast.makeText(getActivity().getApplicationContext(), "There is problem with server, we apologize for this inconvenience.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Connection timed out, check your network.", Toast.LENGTH_LONG).show();
                        }
                        bar_change.setVisibility(View.INVISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("old_password", old_password);
                params.put("new_password", new_password);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


}
