package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentProfileActivity extends AppCompatActivity {

    private EditText etFullName, etClass, etEmail, etPhone;
    private Button btnSave, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        // 1. Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userID = mAuth.getCurrentUser().getUid();

        // 2. Initialize UI
        etFullName = findViewById(R.id.etFullName);
        etClass = findViewById(R.id.etClass);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);

        // 3. Fetch Data from Firestore
        loadFirestoreData();

        // 4. Save Changes (Update Phone)
        btnSave.setOnClickListener(v -> {
            String newPhone = etPhone.getText().toString().trim();
            fStore.collection("Users").document(userID)
                    .update("phone", newPhone)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // 5. Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadFirestoreData() {
        DocumentReference docRef = fStore.collection("Users").document(userID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // IMPORTANT: These strings ("name", "class", etc.) must match your Firestore field names exactly
                etFullName.setText(documentSnapshot.getString("name"));
                etClass.setText(documentSnapshot.getString("class"));
                etEmail.setText(documentSnapshot.getString("email"));
                etPhone.setText(documentSnapshot.getString("phone"));
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show());
    }
}