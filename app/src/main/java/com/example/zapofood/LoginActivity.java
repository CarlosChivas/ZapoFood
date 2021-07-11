package com.example.zapofood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginUsername;
    EditText etLoginPassword;
    Button btnLoginSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginSignIn = findViewById(R.id.btnLoginSignIn);

        btnLoginSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginUsername.getText().toString();
                String password = etLoginPassword.getText().toString();
                //loginUser(username, password);
            }
        });
    }

    /*private void loginUser(String username, String password){
        ParseUser.lo
    }*/
}