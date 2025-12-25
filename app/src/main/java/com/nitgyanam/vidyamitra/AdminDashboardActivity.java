package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.nitgyanam.vidyamitra.R;

public class AdminDashboardActivity extends AppCompatActivity {

    CardView cardEditQuiz, cardStudents, cardCreateQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        cardEditQuiz = findViewById(R.id.cardEditQuiz);
        cardStudents = findViewById(R.id.cardStudents);
        cardCreateQuiz = findViewById(R.id.cardCreateQuiz);

        cardEditQuiz.setOnClickListener(v -> {
//            startActivity(new Intent(this, EditQuizListActivity.class));
        });

        cardStudents.setOnClickListener(v -> {
//            startActivity(new Intent(this, StudentResponsesActivity.class));
        });

        cardCreateQuiz.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateQuizActivity.class));
        });
    }
}
