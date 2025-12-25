package com.nitgyanam.vidyamitra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionVH> {

    private final List<QuestionModel> questionList;

    public QuestionAdapter(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_card, parent, false);
        return new QuestionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionVH holder, int position) {
        QuestionModel q = questionList.get(position);

        holder.tvQuestion.setText(q.questionText);
        holder.tvAnswer.setText("Answer: " + q.correctAnswer);

        holder.btnDelete.setOnClickListener(v -> {
            questionList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, questionList.size());
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class QuestionVH extends RecyclerView.ViewHolder {

        TextView tvQuestion, tvAnswer;
        ImageView btnDelete;

        public QuestionVH(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestionText);
            tvAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            btnDelete = itemView.findViewById(R.id.btnDeleteQuestion);
        }
    }
}
