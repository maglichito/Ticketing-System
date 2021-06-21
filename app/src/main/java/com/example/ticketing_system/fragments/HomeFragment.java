package com.example.ticketing_system.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ticketing_system.R;
import com.example.ticketing_system.data.SharedPrefManager;

public class HomeFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home,container,false);

        return v;
    }

}
