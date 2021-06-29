package com.example.ticketing_system.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.example.ticketing_system.FixtureActivity;
import com.example.ticketing_system.R;
import com.example.ticketing_system.data.API;
import com.example.ticketing_system.data.Fixture;
import com.example.ticketing_system.data.ListAdapterFixture;
import com.example.ticketing_system.data.ListAdapterTicket;
import com.example.ticketing_system.data.Network;
import com.example.ticketing_system.data.SharedPrefManager;
import com.example.ticketing_system.data.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TicketsFragment extends Fragment{


    private ProgressBar bar;
    private TextView errorMessage;
    private API api = new API();
    private Network network;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_tickets,container,false);

        errorMessage = v.findViewById(R.id.error_tickets);
        bar = v.findViewById(R.id.main_progress_tickets);
        bar.setVisibility(View.VISIBLE);
        network = new Network(getActivity());
        if(network.isNetworkAvailable()){
            getTickets();
        }else{
            errorMessage.setText("You are not connected to a network.");
            errorMessage.setVisibility(View.VISIBLE);
        }
        return v;
    }

    public void getTickets() {

        bar.setVisibility(View.VISIBLE);
        String URL = api.getMy_tickets();
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
                                JSONArray arr = obj.getJSONArray("response");

                                ArrayList<Fixture> fixtureArrayList = new ArrayList<>();
                                // On fast switching between fragments app crashes
                                // Because getActivity is null on different fragment
                                if(getActivity() == null){
                                    return;
                                }
                                ListView mListView = (ListView) getActivity().findViewById(R.id.listview_tickets);

                                    for(int i=0; i<arr.length(); i++){

                                        // Init json object
                                        JSONObject o = arr.getJSONObject(i);

                                        // If is round = 1 get matches (We use this as example for project)
                                        // It could be dynamical to get current round
                                            // Creating new fixture
                                            Fixture fixture = new Fixture(o.getString("home_code"),
                                                    o.getString("away_code"),
                                                    o.getString("home_logo"),
                                                    o.getString("away_logo"),
                                                    o.getString("created_at"),
                                                    o.getString("ticket_no"),
                                                    o.getString("time").substring(0,5)
                                                    );
                                            // Adding fixture to arrayList
                                            fixtureArrayList.add(fixture);
                                }
                                ListAdapterTicket adapter = new ListAdapterTicket(getActivity(), R.layout.item_view_tickets, fixtureArrayList);
                                mListView.setAdapter(adapter);
                                bar.setVisibility(View.INVISIBLE);
                            } else {
                                errorMessage.setText(obj.getJSONObject("error").getString("message"));
                                errorMessage.setVisibility(View.VISIBLE);
                                bar.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Server side error.", Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(network.checkConnectivity()){
                            Toast.makeText(getActivity().getApplicationContext(), "There is problem with server, we apologize for this inconvenience.", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.INVISIBLE);
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Connection timed out, check your network.", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.INVISIBLE);
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser().getId() +"");
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }}
