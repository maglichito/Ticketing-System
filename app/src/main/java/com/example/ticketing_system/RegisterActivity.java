package com.example.ticketing_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button = (Button) findViewById(R.id.back_button_reg);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LoginActivity();
            }
        });

    }
    public void LoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}