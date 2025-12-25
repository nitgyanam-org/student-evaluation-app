package com.nitgyanam.vidyamitra;

import android.net.Uri;

public class QuestionModel {

    public String questionText;
    public String optionA, optionB, optionC, optionD;
    public String correctAnswer; // A/B/C/D
    public Uri imageUri; // TEMP (upload later)

    public QuestionModel() {}

    public QuestionModel(String questionText,
                         String optionA,
                         String optionB,
                         String optionC,
                         String optionD,
                         String correctAnswer,
                         Uri imageUri) {

        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.imageUri = imageUri;
    }
}
