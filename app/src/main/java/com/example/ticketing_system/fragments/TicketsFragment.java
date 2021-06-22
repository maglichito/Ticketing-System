package com.example.ticketing_system.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ticketing_system.R;

public class TicketsFragment extends Fragment{

    SwipeRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_tickets,container,false);


        swipe = v.findViewById(R.id.swipeToRefresh_tickets);

        swipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        System.out.println("REFRESH");
                        swipe.setRefreshing(false);
                    }
                }
        );
        return v;
    }
}
