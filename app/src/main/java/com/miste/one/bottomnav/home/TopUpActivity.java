package com.miste.one.bottomnav.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.miste.one.R;
import com.miste.one.startup.LoginActivity;


public class TopUpActivity extends AppCompatActivity {


    Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        continueButton = findViewById(R.id.continueButton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopUpActivity.this, RazorpayPaymentActivity.class);
                startActivity(intent);

            }
        });
    }
}