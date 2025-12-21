package com.example.eduguard.ui.student.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eduguard.R;

import java.util.List;

public class RemainingTimeAdapter extends RecyclerView.Adapter<RemainingTimeAdapter.Holder> {

    private final List<AppRemainingModel> list;

    public RemainingTimeAdapter(List<AppRemainingModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_remaining_time, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        AppRemainingModel m = list.get(pos);
        h.icon.setImageDrawable(m.icon);
        h.tvPackage.setText(m.packageName);
        h.tvUsed.setText("Used: " + m.used + " min");
        h.tvLimit.setText("Daily Limit: " + m.limit + " min");
        h.tvRemaining.setText("Remaining: " + m.remaining + " min");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tvPackage, tvUsed, tvLimit, tvRemaining;

        public Holder(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.ivIcon);
            tvPackage = v.findViewById(R.id.tvPackage);
            tvUsed = v.findViewById(R.id.tvUsed);
            tvLimit = v.findViewById(R.id.tvLimit);
            tvRemaining = v.findViewById(R.id.tvRemaining);
        }
    }
}
