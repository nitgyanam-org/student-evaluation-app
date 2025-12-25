package com.nitgyanam.vidyamitra;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AddQuestionActivity extends AppCompatActivity {

    // UI
    TextView tvSectionTitle, tvNextSectionText;
    EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD;
    Spinner spinnerAnswer;
    ImageView ivPreview;
    CardView btnUploadImage, btnAddQuestion, btnNextSection;
    RecyclerView rvQuestions;

    // Data
    String testTitle;
    ArrayList<String> selectedClasses;
    List<String> sections;
    int currentSectionIndex = 0;

    Map<String, List<QuestionModel>> sectionQuestionMap = new HashMap<>();
    QuestionAdapter questionAdapter;

    Uri selectedImageUri;

    FirebaseFirestore db;
    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        db = FirebaseFirestore.getInstance();

        // ===== RECEIVE DATA =====
        testTitle = getIntent().getStringExtra("TEST_TITLE");
        selectedClasses = getIntent().getStringArrayListExtra("CLASSES");
        sections = getIntent().getStringArrayListExtra("SECTIONS");

        if (testTitle == null || selectedClasses == null || selectedClasses.isEmpty()
                || sections == null || sections.isEmpty()) {
            Toast.makeText(this, "Invalid quiz data", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // ===== IMAGE PICKER =====
        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                selectedImageUri = result.getData().getData();
                                ivPreview.setImageURI(selectedImageUri);
                                ivPreview.setVisibility(View.VISIBLE);
                            }
                        });

        initViews();
        setupInsets();
        setupSpinner();
        setupRecycler();
        setupActions();

        loadSection();
    }

    private void initViews() {
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        tvNextSectionText = findViewById(R.id.tvNextSectionText);

        etQuestion = findViewById(R.id.etQuestion);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);

        spinnerAnswer = findViewById(R.id.spinnerAnswer);
        ivPreview = findViewById(R.id.ivPreview);

        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnNextSection = findViewById(R.id.btnNextSection);

        rvQuestions = findViewById(R.id.rvQuestions);
    }

    private void setupInsets() {
        View root = findViewById(R.id.rootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            v.setPadding(0, 0, 0, bottom);
            return insets;
        });
    }

    // ================= SPINNER WITH HINT =================
    private void setupSpinner() {

        List<String> options = new ArrayList<>();
        options.add("Select correct option");
        options.add("A");
        options.add("B");
        options.add("C");
        options.add("D");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.item_spinner_answer,
                options
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.WHITE);
                return view;
            }
        };

        spinnerAnswer.setAdapter(adapter);
        spinnerAnswer.setSelection(0);
    }

    private void setupRecycler() {
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupActions() {

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnAddQuestion.setOnClickListener(v -> addQuestion());
        btnNextSection.setOnClickListener(v -> moveNext());
    }

    private void loadSection() {
        String section = sections.get(currentSectionIndex);
        tvSectionTitle.setText(section);

        sectionQuestionMap.putIfAbsent(section, new ArrayList<>());

        questionAdapter = new QuestionAdapter(sectionQuestionMap.get(section));
        rvQuestions.setAdapter(questionAdapter);

        tvNextSectionText.setText(
                currentSectionIndex == sections.size() - 1
                        ? "CREATE QUIZ"
                        : "NEXT SECTION"
        );
    }

    private void addQuestion() {

        if (etQuestion.getText().toString().trim().isEmpty()) {
            etQuestion.setError("Question required");
            return;
        }

        if (spinnerAnswer.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select the correct option", Toast.LENGTH_SHORT).show();
            return;
        }

        QuestionModel q = new QuestionModel(
                etQuestion.getText().toString(),
                etOptionA.getText().toString(),
                etOptionB.getText().toString(),
                etOptionC.getText().toString(),
                etOptionD.getText().toString(),
                spinnerAnswer.getSelectedItem().toString(),
                selectedImageUri
        );

        sectionQuestionMap.get(sections.get(currentSectionIndex)).add(q);
        questionAdapter.notifyItemInserted(
                sectionQuestionMap.get(sections.get(currentSectionIndex)).size() - 1
        );

        clearInputs();
    }

    private void clearInputs() {
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        ivPreview.setVisibility(View.GONE);
        selectedImageUri = null;
        spinnerAnswer.setSelection(0);
    }

    private void moveNext() {

        if (sectionQuestionMap.get(sections.get(currentSectionIndex)).isEmpty()) {
            Toast.makeText(this,
                    "Add at least one question in this section",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentSectionIndex == sections.size() - 1) {
            createQuizInFirestore();
            return;
        }

        currentSectionIndex++;
        loadSection();
    }

    // ================= FIRESTORE WRITE =================
    private void createQuizInFirestore() {

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("title", testTitle);
        quizData.put("classes", selectedClasses);
        quizData.put("createdAt", new Date());
        quizData.put("status", "draft");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            quizData.put("createdBy",
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        Map<String, Object> sectionsMap = new HashMap<>();

        for (String section : sections) {

            List<Map<String, Object>> questionsArray = new ArrayList<>();

            for (QuestionModel q : sectionQuestionMap.get(section)) {

                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("questionText", q.questionText);

                Map<String, String> options = new HashMap<>();
                options.put("A", q.optionA);
                options.put("B", q.optionB);
                options.put("C", q.optionC);
                options.put("D", q.optionD);

                questionMap.put("options", options);
                questionMap.put("correctAnswer", q.correctAnswer);
                questionMap.put("imageUrl", null);

                questionsArray.add(questionMap);
            }

            Map<String, Object> sectionMap = new HashMap<>();
            sectionMap.put("questions", questionsArray);
            sectionsMap.put(section, sectionMap);
        }

        quizData.put("sections", sectionsMap);

        db.collection("quizzes")
                .add(quizData)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this,
                            "Quiz created successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, AdminDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}
