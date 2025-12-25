package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // 1. Find the profile icon by the ID we defined in the XML
        ImageView ivProfile = findViewById(R.id.ivProfile);

        // 2. Set a click listener to perform the jump to the Profile page
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate from Dashboard to StudentProfileActivity
                Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}