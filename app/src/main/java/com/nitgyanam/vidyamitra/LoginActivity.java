package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBtn;
    TextView signupTv;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // Data from Signup (students only)
    String studentName, studentClass, studentEmailFromSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        signupTv = findViewById(R.id.signupTv);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Receive data from Signup
        Intent incomingIntent = getIntent();
        studentName = incomingIntent.getStringExtra("student_name");
        studentClass = incomingIntent.getStringExtra("student_class");
        studentEmailFromSignup = incomingIntent.getStringExtra("student_email");


        if (studentEmailFromSignup != null) {
            emailEt.setText(studentEmailFromSignup);
        }

        loginBtn.setOnClickListener(v -> loginUser());
        signupTv.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) {
                        Toast.makeText(this,
                                "Authentication error",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 🔥 DO NOT CHECK email verification YET
                    handleUserAfterLogin(user);
                });
    }


    /**
     * Handles admin vs student and first-login logic
     */
    private void handleUserAfterLogin(FirebaseUser user) {

        String uid = user.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    // 🔹 Admin already exists in Firestore
                    if (documentSnapshot.exists()) {

                        String role = documentSnapshot.getString("role");

                        if ("admin".equals(role)) {
                            // 👑 Admin → skip email verification
                            goToAdminDashboard();
                            return;
                        }

                        // 👨‍🎓 Student → MUST verify email
                        if (!user.isEmailVerified()) {
                            Toast.makeText(this,
                                    "Please verify your email first.",
                                    Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            return;
                        }

                        // Existing verified student
                        goToStudentDashboard();
                    }
                    else {
                        // 🔹 First login → student
                        if (!user.isEmailVerified()) {
                            Toast.makeText(this,
                                    "Please verify your email first.",
                                    Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            return;
                        }

                        createStudentProfile(user);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }


    /**
     * Creates Firestore profile ONLY for students (first login)
     */
    private void createStudentProfile(FirebaseUser user) {

        String uid = user.getUid();

        Map<String, Object> student = new HashMap<>();
        student.put("uid", uid);
        student.put("email", user.getEmail());
        student.put("role", "student");
        student.put("name", studentName != null ? studentName : "");
        student.put("class", studentClass != null ? studentClass : "");
        student.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .set(student)
                .addOnSuccessListener(aVoid -> goToStudentDashboard())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to save student data",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void goToStudentDashboard() {
        Toast.makeText(this,
                "Login successful",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAdminDashboard() {
        Toast.makeText(this,
                "Admin login successful",
                Toast.LENGTH_SHORT).show();

//        startActivity(new Intent(this, AdminDashboardActivity.class));
        finish();
    }
}
