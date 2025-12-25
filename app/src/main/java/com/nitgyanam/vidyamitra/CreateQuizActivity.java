package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class CreateQuizActivity extends AppCompatActivity {

    EditText etTestTitle, etSectionName;
    CardView btnAddSection, btnStartAddingQuestions;
    RecyclerView rvSections, rvClasses;

    List<String> sectionList;
    SectionsAdapter sectionAdapter;
    ClassSelectionAdapter classAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        etTestTitle = findViewById(R.id.etTestTitle);
        etSectionName = findViewById(R.id.etSectionName);
        btnAddSection = findViewById(R.id.btnAddSection);
        btnStartAddingQuestions = findViewById(R.id.btnStartAddingQuestions);
        rvSections = findViewById(R.id.rvSections);
        rvClasses = findViewById(R.id.rvClasses);

        NestedScrollView scrollRoot = findViewById(R.id.scrollRoot);

        ViewCompat.setOnApplyWindowInsetsListener(scrollRoot, (view, insets) -> {

            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int systemBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            // Apply bottom padding dynamically
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    imeHeight > 0 ? imeHeight : systemBarHeight
            );

            return insets;
        });

        // ===== CLASS SELECTION =====
        List<String> classList = Arrays.asList(
                "Class 1", "Class 2", "Class 3",
                "Class 4", "Class 5",
                "Class 6", "Class 7", "Class 8", "Class 9", "Class 10"
        );

        classAdapter = new ClassSelectionAdapter(classList);
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        rvClasses.setAdapter(classAdapter);

        // ===== SECTIONS =====
        sectionList = new ArrayList<>();
        sectionAdapter = new SectionsAdapter(sectionList);
        rvSections.setLayoutManager(new LinearLayoutManager(this));
        rvSections.setAdapter(sectionAdapter);

        btnAddSection.setOnClickListener(v -> {
            String sectionName = etSectionName.getText().toString().trim();
            if (sectionName.isEmpty()) {
                etSectionName.setError("Section required");
                return;
            }
            sectionList.add(sectionName);
            sectionAdapter.notifyItemInserted(sectionList.size() - 1);
            etSectionName.setText("");
        });

        btnStartAddingQuestions.setOnClickListener(v -> {

            String testTitle = etTestTitle.getText().toString().trim();
            List<String> selectedClasses = classAdapter.getSelectedClasses();

            if (testTitle.isEmpty()) {
                etTestTitle.setError("Test title required");
                return;
            }

            if (selectedClasses.isEmpty()) {
                Toast.makeText(this, "Select at least one class", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sectionList.isEmpty()) {
                Toast.makeText(this, "Add at least one section", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("TEST_TITLE", testTitle);
            intent.putStringArrayListExtra("CLASSES",
                    new ArrayList<>(selectedClasses));
            intent.putStringArrayListExtra("SECTIONS",
                    new ArrayList<>(sectionList));
            startActivity(intent);
        });
    }
}
