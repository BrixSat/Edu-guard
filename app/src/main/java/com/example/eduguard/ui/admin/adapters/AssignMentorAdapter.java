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

public class AssignMentorAdapter extends RecyclerView.Adapter<AssignMentorAdapter.MentorVH> {

    public interface MentorAssignListener {
        void onAssign(User mentor);
    }

    private final List<User> mentors;
    private final MentorAssignListener listener;

    public AssignMentorAdapter(List<User> mentors, MentorAssignListener listener) {
        this.mentors = mentors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MentorVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MentorVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assign_mentor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MentorVH holder, int position) {
        User mentor = mentors.get(position);

        holder.name.setText(mentor.getName());
        holder.email.setText(mentor.getEmail());

        holder.btnAssign.setOnClickListener(v -> listener.onAssign(mentor));
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    static class MentorVH extends RecyclerView.ViewHolder {
        TextView name, email;
        Button btnAssign;

        public MentorVH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvMentorName);
            email = itemView.findViewById(R.id.tvMentorEmail);
            btnAssign = itemView.findViewById(R.id.btnAssignMentor);
        }
    }
}
