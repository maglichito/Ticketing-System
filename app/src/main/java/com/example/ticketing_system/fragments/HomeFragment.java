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
import android.widget.Toast;

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
import com.example.ticketing_system.data.ListAdapter;
import com.example.ticketing_system.data.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment{


    private API api = new API();
    private ProgressBar bar;
    private Network network;
    private TextView errorMessage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home,container,false);

        bar = v.findViewById(R.id.main_progress);
        errorMessage = v.findViewById(R.id.error);
        network = new Network(getActivity());

        // If network is not available show error message
        if(network.isNetworkAvailable()){
            getFixtures();
        }else{
            bar.setVisibility(View.INVISIBLE);
            errorMessage.setText("You are not connected to network.");
            errorMessage.setVisibility(View.VISIBLE);
        }
        return v;
    }

    // This method will pull all fixtures
    public void getFixtures(){
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
                                        Fixture fixture = new Fixture(o.getString("home").substring(0,3).toUpperCase(),
                                                o.getString("away").substring(0,3).toUpperCase(),
                                                o.getString("date"),
                                                o.getString("time").substring(0,5).toUpperCase(),
                                                o.getString("home_logo"),
                                                o.getString("away_logo"));

                                        // Adding fixture to arrayList
                                        fixtureArrayList.add(fixture);
                                        // Adding event on item
                                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent i = new Intent(getActivity(), FixtureActivity.class);
                                                startActivity(i);
                                            }
                                        });
                                    }
                                }

                                ListAdapter adapter = new ListAdapter(getActivity(), R.layout.item_view, fixtureArrayList);
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
