package com.nitgyanam.vidyamitra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClassSelectionAdapter
        extends RecyclerView.Adapter<ClassSelectionAdapter.ClassViewHolder> {

    private final List<String> classes;
    private final List<String> selectedClasses = new ArrayList<>();

    public ClassSelectionAdapter(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getSelectedClasses() {
        return selectedClasses;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_checkbox, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String className = classes.get(position);
        holder.cbClass.setText(className);

        holder.cbClass.setOnCheckedChangeListener(null);
        holder.cbClass.setChecked(selectedClasses.contains(className));

        holder.cbClass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedClasses.contains(className)) {
                    selectedClasses.add(className);
                }
            } else {
                selectedClasses.remove(className);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbClass;

        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            cbClass = itemView.findViewById(R.id.cbClass);
        }
    }
}
