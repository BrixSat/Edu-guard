package com.example.eduguard.ui.admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.Policy;

import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private final List<Policy.AppRule> rules;

    public PolicyAdapter(List<Policy.AppRule> rules) {
        this.rules = rules;
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PolicyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_policy_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyViewHolder holder, int position) {

        Policy.AppRule rule = rules.get(position);

        holder.tvPackage.setText(rule.getPackageName());
        holder.tvTime.setText("Daily: " + rule.getDailyLimitMinutes() + " min");

        // Load app icon
        loadIcon(holder.itemView.getContext(), rule.getPackageName(), holder.icon);

        holder.switchAllowed.setChecked(!rule.isBlocked());
        holder.switchAllowed.setOnCheckedChangeListener((b, checked) -> {
            rule.setBlocked(!checked);
        });

        holder.itemView.setOnClickListener(v -> showTimeDialog(v.getContext(), rule, holder));
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    private void showTimeDialog(Context ctx, Policy.AppRule rule, PolicyViewHolder holder) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        b.setTitle("Set Daily Limit (Minutes)");

        EditText input = new EditText(ctx);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(rule.getDailyLimitMinutes()));

        b.setView(input);

        b.setPositiveButton("Save", (d, w) -> {
            int min = Integer.parseInt(input.getText().toString());
            rule.setDailyLimitMinutes(min);
            holder.tvTime.setText("Daily: " + min + " min");
        });

        b.setNegativeButton("Cancel", null);
        b.show();
    }

    private void loadIcon(Context ctx, String pkg, ImageView img) {
        try {
            Drawable icon = ctx.getPackageManager().getApplicationIcon(pkg);
            img.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            img.setImageResource(R.drawable.ic_unknown);
        }
    }

    static class PolicyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tvPackage, tvTime;
        Switch switchAllowed;

        public PolicyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivAppIcon);
            tvPackage = itemView.findViewById(R.id.tvPackageName);
            tvTime = itemView.findViewById(R.id.tvTimeLimit);
            switchAllowed = itemView.findViewById(R.id.switchAllowed);
        }
    }
}
