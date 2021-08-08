package com.example.zapofood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnCancelSignUp;
    private Button btnConfirmSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCancelSignUp = findViewById(R.id.btnCancelSignUp);
        btnConfirmSignUp = findViewById(R.id.btnConfirmSignUp);

        btnCancelSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnConfirmSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }

    public void signUpUser(){
        if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords must be equals", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    //Issue with signUp
                    Log.e(TAG, "Error with signUp");
                    Toast.makeText(SignUpActivity.this, "Error with signUp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}