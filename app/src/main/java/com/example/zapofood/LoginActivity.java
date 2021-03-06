package com.example.zapofood;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zapofood.fragments.HomeFragment;
import com.facebook.login.Login;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


import android.app.ProgressDialog;


import com.parse.facebook.ParseFacebookUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUsername;
    private EditText etLoginPassword;
    private Button btnLoginSignIn;
    private TextView tvButtonNewAccount;
    private CardView btnLoginFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginSignIn = findViewById(R.id.btnLoginSignIn);
        btnLoginFacebook = findViewById(R.id.containerFacebookLogin);
        tvButtonNewAccount = findViewById(R.id.tvButtonNewAccount);

        btnLoginSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginUsername.getText().toString();
                String password = etLoginPassword.getText().toString();
                loginUser(username, password);
            }
        });

        tvButtonNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLoginFacebook.setOnClickListener(v -> {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Please, wait a moment.");
            dialog.setMessage("Logging in...");
            dialog.show();
            Collection<String> permissions = Arrays.asList("public_profile", "email");

            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    dialog.dismiss();
                    if(e!=null){
                        Log.d("Login", "todo mal" + e.toString());
                    }else{
                        if(user == null){
                            Log.d("Login", "Usuario null");
                        }else{
                            Collection<String> permissions = Arrays.asList("public_profile", "email");
                            if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                                ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), LoginActivity.this, permissions, ex -> {
                                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                                        Toast.makeText(LoginActivity.this, "Woohoo, user logged in with Facebook.", Toast.LENGTH_LONG).show();
                                        Log.d("FacebookLoginExample", "Woohoo, user logged in with Facebook!");
                                    }
                                });
                            }
                            goMainActivity();
                        }
                    }
                }
            });
        });
    }

    private void loginUser(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Toast.makeText(LoginActivity.this, "Error with Login!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Navigate to the main activity if the user has signed in properly
                if(ParseUser.getCurrentUser().getString("type").equals("user")){
                    goMainActivity();
                }
                else if (ParseUser.getCurrentUser().getString("type").equals("owner")){
                    goMainActivity();
                }
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Go to the main activity
    private void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}