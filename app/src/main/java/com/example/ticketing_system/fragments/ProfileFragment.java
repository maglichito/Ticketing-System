package com.example.ticketing_system.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ticketing_system.MainActivity;
import com.example.ticketing_system.R;
import com.example.ticketing_system.data.API;
import com.example.ticketing_system.data.SharedPrefManager;
import com.example.ticketing_system.data.User;
import com.example.ticketing_system.data.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment{

    TextView name,lastname,username,email;
    ProgressBar bar;
    EditText oldpassword,newpassword;
    User u;
    API api = new API();
    Button changeButton,logoutButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_profile,container,false);

        name = v.findViewById(R.id.name_profile);
        lastname = v.findViewById(R.id.lastname_profile);
        username = v.findViewById(R.id.username_profile);
        email = v.findViewById(R.id.email_profile);
        oldpassword = v.findViewById(R.id.oldpass_change_profile);
        newpassword = v.findViewById(R.id.newpass_change_profile);
        bar = v.findViewById(R.id.progress_bar_profile);
        bar.setVisibility(View.INVISIBLE);
        u = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        name.setText("Ime: " + u.getName());
        lastname.setText("Prezime: " + u.getLastname());
        username.setText("Korisničko ime: " + u.getUsername());
        email.setText("E-mail: " + u.getEmail());
        changeButton = v.findViewById(R.id.change_button);
        logoutButton = v.findViewById(R.id.logout_profile);

        changeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                changePassword();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).logout();
            }
        });

        return v;
    }
    private void changePassword() {

        //URL
        String URL = api.getPass_change();
        //first getting the values
        final String id =  Integer.toString(u.getId());
        final String old_password = oldpassword.getText().toString();
        final String new_password = newpassword.getText().toString();
        //validating inputs
        if (TextUtils.isEmpty(old_password)) {
            oldpassword.setError("Unesite staru lozinku!");
            oldpassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(new_password) || new_password.length() < 5) {
            newpassword.setError("Unesite novu lozinku!");
            newpassword.requestFocus();
            return;
        }
        if (old_password.equals(new_password)) {
            newpassword.setError("Stara i nova lozinka su identicne!");
            newpassword.requestFocus();
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

                                Toast.makeText(getActivity().getApplicationContext(), obj.getJSONObject("response").getString("message"), Toast.LENGTH_SHORT).show();
                                bar.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
