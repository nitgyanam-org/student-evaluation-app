package com.nitgyanam.vidyamitra; // change if needed

import com.google.firebase.FirebaseApp;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        FirebaseApp.initializeApp(this);


        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailEt.setError("Email required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordEt.setError("Password required");
                return;
            }

            // Temporary success action
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();

            // Later: Firebase / API login logic here
        });
    }
}
