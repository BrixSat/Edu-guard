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

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.MentorViewHolder> {

    public interface MentorActionListener {
        void onEdit(User mentor);
        void onDelete(User mentor);
        void onAssign(User mentor);
    }

    private final List<User> mentorList;
    private final MentorActionListener listener;

    public MentorAdapter(List<User> mentorList, MentorActionListener listener) {
        this.mentorList = mentorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MentorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MentorViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentor, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MentorViewHolder holder, int position) {
        User mentor = mentorList.get(position);

        holder.name.setText(mentor.getName());
        holder.email.setText(mentor.getEmail());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(mentor));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(mentor));
        holder.btnAssign.setOnClickListener(v -> listener.onAssign(mentor));
    }

    @Override
    public int getItemCount() {
        return mentorList.size();
    }

    public static class MentorViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;
        ImageButton btnEdit, btnDelete, btnAssign;

        public MentorViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvMentorName);
            email = itemView.findViewById(R.id.tvMentorEmail);
            btnEdit = itemView.findViewById(R.id.btnEditMentor);
            btnDelete = itemView.findViewById(R.id.btnDeleteMentor);
            btnAssign = itemView.findViewById(R.id.btnAssignStudents);
        }
    }
}
