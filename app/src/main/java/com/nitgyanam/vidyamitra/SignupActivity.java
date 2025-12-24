package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    EditText nameEt, emailEt, passwordEt, confirmPasswordEt;
    Spinner classSpinner;
    Button signupBtn;
    TextView loginRedirectTv;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        classSpinner = findViewById(R.id.classSpinner);
        signupBtn = findViewById(R.id.signupBtn);
        loginRedirectTv = findViewById(R.id.loginRedirectTv);

        // Spinner data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Select Class","Class 1","Class 2","Class 3","Class 4","Class 5","Class 6","Class 7","Class 8","Class 9","Class 10"}
        );
        classSpinner.setAdapter(adapter);

        signupBtn.setOnClickListener(v -> validateAndConfirm());

        loginRedirectTv.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void validateAndConfirm() {
        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();
        String selectedClass = classSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                selectedClass.equals("Select Class")) {

            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            passwordEt.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEt.setError("Passwords do not match");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure to move ahead?")
                .setNegativeButton("No, take me back", null)
                .setPositiveButton("Yes", (dialog, which) -> createAccount(email, password))
                .show();
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            Toast.makeText(this,
                                                    "Verification email sent. Please verify before login.",
                                                    Toast.LENGTH_LONG).show();

                                            mAuth.signOut();
                                            Intent intent = new Intent(this, LoginActivity.class);
                                            intent.putExtra("student_name", nameEt.getText().toString().trim());
                                            intent.putExtra("student_class", classSpinner.getSelectedItem().toString());
                                            intent.putExtra("student_email", emailEt.getText().toString().trim());
                                            startActivity(intent);
                                            finish();

                                        }
                                    });
                        }

                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
