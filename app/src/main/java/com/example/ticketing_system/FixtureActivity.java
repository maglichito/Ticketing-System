package com.example.ticketing_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.ticketing_system.databinding.ActivityFixtureBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FixtureActivity extends AppCompatActivity {

    private  ActivityFixtureBinding binding_fixture;
    private Network network = new Network(this);
    private String fixture_id;
    private String ticket_type;
    private API api = new API();
    private int tickets = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding_fixture = ActivityFixtureBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_fixture);
        setContentView(binding_fixture.getRoot());
        statusBar();

        binding_fixture.progressBarBuy.setVisibility(View.INVISIBLE);
        Intent intent = this.getIntent();

        if(intent !=null){

            fixture_id = intent.getStringExtra("id");
            availableSeats();
            String home_logo_url = intent.getStringExtra("home_logo_url");
            String away_logo_url = intent.getStringExtra("away_logo_url");

            String time = intent.getStringExtra("time");
            String date = intent.getStringExtra("date");
            String stadium = intent.getStringExtra("stadium").toUpperCase();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(FixtureActivity.this,R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.stand));
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            binding_fixture.spinner.setAdapter(adapter);
            binding_fixture.timeItem.setText(time);
            binding_fixture.dateItem.setText(date);
            binding_fixture.stadiumText.setText(stadium);
            binding_fixture.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if(position == 0){
                        ticket_type = "3";
                    }
                    else if(position == 1){
                        ticket_type = "4";
                    }
                    else if(position == 2){
                        ticket_type = "1";
                    }
                    else if(position == 3){
                        ticket_type = "2";
                    }
                    else {
                        ticket_type = "5";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            binding_fixture.backButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });

            binding_fixture.buyButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if(tickets == 0){
                        Toast.makeText(getApplicationContext(), "All tickets are sold out.", Toast.LENGTH_SHORT).show();
                    }
                    if(network.isNetworkAvailable()){
                        buyTicket();
                    }else{
                        Toast.makeText(getApplicationContext(), "You are not connected to a network.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Picasso.get().load(home_logo_url).into(binding_fixture.fixtureHomeLogo);
            Picasso.get().load(away_logo_url).into(binding_fixture.fixtureAwayLogo);

        }
    }
    private void statusBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.bright_red,this.getTheme()));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.bright_red));
        }
    }
    public void availableSeats(){
        String URL = api.getAvailable_tickets();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressBar.setVisibility(View.GONE);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getJSONObject("error").getBoolean("error")) {
                                tickets = obj.getJSONObject("response").getInt("stadium_capacity") - obj.getJSONObject("response").getInt("bought_tickets");
                                binding_fixture.buyButton.setText("BUY ("+tickets + ")");
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Server side error.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(network.checkConnectivity()){
                            Toast.makeText(getApplicationContext(), "There is problem with server, we apologize for this inconvenience.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Connection timed out, check your network.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fixture_id", fixture_id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    public void buyTicket(){
        binding_fixture.progressBarBuy.setVisibility(View.VISIBLE);
        String URL = api.getBuy_ticket();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressBar.setVisibility(View.GONE);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getJSONObject("error").getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                                binding_fixture.progressBarBuy.setVisibility(View.INVISIBLE);
                                availableSeats();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                                binding_fixture.progressBarBuy.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Server side error.", Toast.LENGTH_SHORT).show();
                            binding_fixture.progressBarBuy.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(network.checkConnectivity()){
                            Toast.makeText(getApplicationContext(), "There is problem with server, we apologize for this inconvenience.", Toast.LENGTH_LONG).show();
                            binding_fixture.progressBarBuy.setVisibility(View.INVISIBLE);
                        }else{
                            Toast.makeText(getApplicationContext(), "Connection timed out, check your network.", Toast.LENGTH_LONG).show();
                            binding_fixture.progressBarBuy.setVisibility(View.INVISIBLE);
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fixture_id", fixture_id);
                params.put("ticket_type_id", ticket_type);
                params.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUser().getId() +"");
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    }