package com.example.varta.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.varta.databinding.ActivityPhoneNumberEnterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class phone_number_enter extends AppCompatActivity {

    ActivityPhoneNumberEnterBinding binding;
    FirebaseAuth auth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberEnterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //If the number is verified then opening the MainActivity
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(phone_number_enter.this, MainActivity.class);
            startActivity(intent);
            finish();
        }



        //To hide the top action bar
        getSupportActionBar().hide();

        //To request focus to the text box so that the keyboard which is set visible in the manifest file is visible
        binding.phoneNumberTextBox.requestFocus();


        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(phone_number_enter.this, otp_verification.class);
                intent.putExtra("phoneNumber", binding.phoneNumberTextBox.getText().toString());
                startActivity(intent);
            }
        });
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
}