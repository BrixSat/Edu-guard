package com.example.eduguard.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.User;

import java.util.List;

public class AssignStudentAdapter extends RecyclerView.Adapter<AssignStudentAdapter.AssignViewHolder> {

    public interface AssignListener {
        void onAssign(User student);
    }

    private final List<User> students;
    private final AssignListener listener;

    public AssignStudentAdapter(List<User> students, AssignListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_assign_student, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AssignViewHolder holder, int position) {
        User student = students.get(position);

        holder.name.setText(student.getName());
        holder.email.setText(student.getEmail());

        holder.btnAssign.setOnClickListener(v -> listener.onAssign(student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class AssignViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        Button btnAssign;

        public AssignViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvStudentName);
            email = itemView.findViewById(R.id.tvStudentEmail);
            btnAssign = itemView.findViewById(R.id.btnAssign);
        }
    }
}
