package com.example.eduguard.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.User;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentVH> {

    public interface StudentActionListener {
        void onEdit(User student);
        void onDelete(User student);
        void onAssignMentor(User student);
    }

    private final List<User> students;
    private final StudentActionListener listener;

    public StudentAdapter(List<User> students, StudentActionListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentVH holder, int position) {
        User s = students.get(position);

        holder.name.setText(s.getName());
        holder.email.setText(s.getEmail());
        holder.mentor.setText(s.getAssignedMentor() == null ? "No mentor" : "Mentor Assigned");

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(s));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(s));
        holder.btnAssignMentor.setOnClickListener(v -> listener.onAssignMentor(s));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentVH extends RecyclerView.ViewHolder {
        TextView name, email, mentor;
        ImageButton btnEdit, btnDelete, btnAssignMentor;

        public StudentVH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvStudentName);
            email = itemView.findViewById(R.id.tvStudentEmail);
            mentor = itemView.findViewById(R.id.tvStudentMentor);

            btnEdit = itemView.findViewById(R.id.btnEditStudent);
            btnDelete = itemView.findViewById(R.id.btnDeleteStudent);
            btnAssignMentor = itemView.findViewById(R.id.btnAssignMentor);
        }
    }
}
