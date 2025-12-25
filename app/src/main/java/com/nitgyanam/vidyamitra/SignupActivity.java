package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    EditText nameEt, emailEt, passwordEt, confirmPasswordEt;
    Spinner classSpinner;
    Button signupBtn;
    TextView alreadyUserTv, loginTv;

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
        alreadyUserTv = findViewById(R.id.alreadyUserTv);
        loginTv = findViewById(R.id.loginTv);

        setupClassSpinner();

        signupBtn.setOnClickListener(v -> validateAndConfirm());

        loginTv.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Spinner setup with custom layouts
     */
    private void setupClassSpinner() {

        List<String> classes = new ArrayList<>();
        classes.add("Select Class");
        classes.add("Class 1");
        classes.add("Class 2");
        classes.add("Class 3");
        classes.add("Class 4");
        classes.add("Class 5");
        classes.add("Class 6");
        classes.add("Class 7");
        classes.add("Class 8");
        classes.add("Class 9");
        classes.add("Class 10");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner_class,              // YOUR custom layout
                classes
        );

        adapter.setDropDownViewResource(R.layout.item_spinner_class_dropdown);
        classSpinner.setAdapter(adapter);

        // Make "Select Class" look like a hint
        classSpinner.setSelection(0, false);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.parseColor("#999999")); // hint color
                } else {
                    tv.setTextColor(Color.parseColor("#2E004F")); // normal text color
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void validateAndConfirm() {
        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();
        String selectedClass = classSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) ||
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
                .setPositiveButton("Yes", (dialog, which) ->
                        createAccount(email, password))
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

                                            Toast.makeText(
                                                    this,
                                                    "Verification email sent. Please verify before login.",
                                                    Toast.LENGTH_LONG
                                            ).show();

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
                        Toast.makeText(
                                this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Signup failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
