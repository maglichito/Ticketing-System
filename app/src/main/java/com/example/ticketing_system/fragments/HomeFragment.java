package com.example.ticketing_system.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketing_system.FixtureActivity;
import com.example.ticketing_system.R;
import com.example.ticketing_system.data.API;
import com.example.ticketing_system.data.Fixture;
import com.example.ticketing_system.data.ListAdapterFixture;
import com.example.ticketing_system.data.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment{


    private API api = new API();
    private ProgressBar bar;
    private Network network;
    private TextView errorMessage,currentWeek,allFixtures;

    private boolean loaded = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home,container,false);

        bar = v.findViewById(R.id.main_progress);
        errorMessage = v.findViewById(R.id.error);
        network = new Network(getActivity());
        currentWeek = (TextView) v.findViewById(R.id.matchweek);
        allFixtures = (TextView) v.findViewById(R.id.see_all_fixtures);
        // If network is not available show error message
        if(network.isNetworkAvailable()){
            getFixtures();
        }else{
            bar.setVisibility(View.INVISIBLE);
            errorMessage.setText("You are not connected to network.");
            errorMessage.setVisibility(View.VISIBLE);
        }

        currentWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // If network is not available show error message
                if(network.isNetworkAvailable()){
                    getFixtures();
                }else{
                    bar.setVisibility(View.INVISIBLE);
                    errorMessage.setText("You are not connected to network.");
                    errorMessage.setVisibility(View.VISIBLE);
                }

            }
        });
        allFixtures.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // If network is not available show error message
                if(network.isNetworkAvailable()){
                    getAllFixtures();
                }else{
                    bar.setVisibility(View.INVISIBLE);
                    errorMessage.setText("You are not connected to network.");
                    errorMessage.setVisibility(View.VISIBLE);
                }

            }
        });
        return v;
    }

    // This method will pull first matchweek
    public void getFixtures(){

        if(loaded){
            ListView mListView = (ListView) getActivity().findViewById(R.id.listview);
            ListAdapterFixture adapter = new ListAdapterFixture(getActivity(), R.layout.item_view, new ArrayList<Fixture>());
            mListView.setAdapter(adapter);
        }
        bar.setVisibility(View.VISIBLE);
        String URL = api.getFixtures();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getJSONObject("error").getBoolean("error")) {

                                JSONArray arr = obj.getJSONArray("fixtures");

                                ArrayList<Fixture> fixtureArrayList = new ArrayList<>();
                                // On fast switching between fragments app crashes
                                // Because getActivity is null on different fragment
                                if(getActivity() == null){
                                    return;
                                }
                                ListView mListView = (ListView) getActivity().findViewById(R.id.listview);
                                for(int i=0; i<arr.length(); i++){

                                    // Init json object
                                    JSONObject o = arr.getJSONObject(i);

                                    // If is round = 1 get matches (We use this as example for project)
                                    // It could be dynamical to get current round
                                    if(o.getString("round").equalsIgnoreCase("1")){
                                        // Creating new fixture
                                        Fixture fixture = new Fixture(o.getString("fixture_id"),
                                                o.getString("home_code"),
                                                o.getString("away_code"),
                                                o.getString("date"),
                                                o.getString("time").substring(0,5).toUpperCase(),
                                                o.getString("home_logo"),
                                                o.getString("away_logo"),
                                                o.getString("stadium"));

                                        // Adding fixture to arrayList
                                        fixtureArrayList.add(fixture);
                                        // Adding event on item
                                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent intent = new Intent(getActivity(), FixtureActivity.class);

                                                // Passing data to intent
                                                intent.putExtra("id",fixtureArrayList.get(position).getId());
                                                intent.putExtra("stadium",fixtureArrayList.get(position).getStadium());
                                                intent.putExtra("home_logo_url",fixtureArrayList.get(position).getHome_logo());
                                                intent.putExtra("away_logo_url",fixtureArrayList.get(position).getAway_logo());
                                                intent.putExtra("time",fixtureArrayList.get(position).getTime());
                                                intent.putExtra("date",fixtureArrayList.get(position).getDate());
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }

                                ListAdapterFixture adapter = new ListAdapterFixture(getActivity(), R.layout.item_view, fixtureArrayList);
                                mListView.setAdapter(adapter);
                                bar.setVisibility(View.INVISIBLE);
                                loaded = true;
                            } else {
                                bar.setVisibility(View.INVISIBLE);
                                errorMessage.setVisibility(View.VISIBLE);
                                errorMessage.setText(obj.getJSONObject("error").getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(network.checkConnectivity()){
                    errorMessage.setText("There is problem with server, we apologize for this inconvenience.");
                    bar.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                }else{
                    errorMessage.setText("Connection timed out, check your network.");
                    bar.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    // This method will pull all fixtures
    public void getAllFixtures(){

        // Setting listview to empty array before again instantiating
        ListView mListView = (ListView) getActivity().findViewById(R.id.listview);
        ListAdapterFixture adapter = new ListAdapterFixture(getActivity(), R.layout.item_view, new ArrayList<Fixture>());
        mListView.setAdapter(adapter);
        bar.setVisibility(View.VISIBLE);

        String URL = api.getFixtures();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getJSONObject("error").getBoolean("error")) {

                                JSONArray arr = obj.getJSONArray("fixtures");

                                ArrayList<Fixture> fixtureArrayList = new ArrayList<>();
                                // On fast switching between fragments app crashes
                                // Because getActivity is null on different fragment
                                if(getActivity() == null){
                                    return;
                                }
                                ListView mListView = (ListView) getActivity().findViewById(R.id.listview);
                                for(int i=0; i<arr.length(); i++){

                                    // Init json object
                                    JSONObject o = arr.getJSONObject(i);

                                    // If is round = 1 get matches (We use this as example for project)
                                    // It could be dynamical to get current round
                                        // Creating new fixture
                                        Fixture fixture = new Fixture(o.getString("fixture_id"),
                                                o.getString("home_code"),
                                                o.getString("away_code"),
                                                o.getString("date"),
                                                o.getString("time").substring(0,5).toUpperCase(),
                                                o.getString("home_logo"),
                                                o.getString("away_logo"),
                                                o.getString("stadium"));

                                        // Adding fixture to arrayList
                                        fixtureArrayList.add(fixture);
                                        // Adding event on item
                                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent intent = new Intent(getActivity(), FixtureActivity.class);

                                                // Passing data to intent
                                                intent.putExtra("id",fixtureArrayList.get(position).getId());
                                                intent.putExtra("stadium",fixtureArrayList.get(position).getStadium());
                                                intent.putExtra("home_logo_url",fixtureArrayList.get(position).getHome_logo());
                                                intent.putExtra("away_logo_url",fixtureArrayList.get(position).getAway_logo());
                                                intent.putExtra("time",fixtureArrayList.get(position).getTime());
                                                intent.putExtra("date",fixtureArrayList.get(position).getDate());
                                                startActivity(intent);
                                            }
                                        });
                                }
                                ListAdapterFixture adapter = new ListAdapterFixture(getActivity(), R.layout.item_view, fixtureArrayList);
                                bar.setVisibility(View.INVISIBLE);
                                mListView.setAdapter(adapter);
                            } else {
                                bar.setVisibility(View.INVISIBLE);
                                errorMessage.setVisibility(View.VISIBLE);
                                errorMessage.setText(obj.getJSONObject("error").getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(network.checkConnectivity()){
                    errorMessage.setText("There is problem with server, we apologize for this inconvenience.");
                    bar.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                }else{
                    errorMessage.setText("Connection timed out, check your network.");
                    bar.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
