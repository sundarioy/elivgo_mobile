package com.ta.elivgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login (View view) {
        String email, password;
        email = getEmail();
        password = getPassword();
        if (email.equals("user@email.com") && password.equals("12345678") ) {
            Thread myThread = new Thread() {
                public void run() {
                    try {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
        } else {
            Toast.makeText(this, "Akun tidak terdaftar", Toast.LENGTH_SHORT).show();
        }
    }

    private String getEmail() {
        EditText et_email = findViewById(R.id.et_email);
        return et_email.getText().toString();
    }

    private String getPassword() {
        EditText et_password = findViewById(R.id.et_password);
        return et_password.getText().toString();
    }

    public void register(View view){
        Intent i = new Intent(getApplicationContext(), com.ta.elivgo.RegisterActivity.class);
        startActivity(i);
        finish();
    }

}