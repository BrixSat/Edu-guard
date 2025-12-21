package com.example.eduguard.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.ExtraTimeRequest;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    // Listener for approve/reject actions
    public interface OnRequestActionListener {
        void onApprove(ExtraTimeRequest req);
        void onReject(ExtraTimeRequest req);
    }

    private final List<ExtraTimeRequest> items;
    private final OnRequestActionListener listener;

    public RequestAdapter(List<ExtraTimeRequest> items, OnRequestActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ExtraTimeRequest item = items.get(position);

        // TYPE
        String typeText = item.getType().equals("extraTime")
                ? "Extra Time Request"
                : "Emergency Request";

        holder.tvType.setText(typeText);

        // Student
        holder.tvStudent.setText("Student: " + item.getStudentId());

        // Build message
        String msg = "";

        if (item.getRequestedForApp() != null) {
            msg += "App: " + item.getRequestedForApp() + "\n";
        }

        if (item.getRequestedExtraMinutes() != null) {
            msg += "Extra Minutes: " + item.getRequestedExtraMinutes() + "\n";
        }

        msg += "Message: " + item.getMessage();

        holder.tvMessage.setText(msg);
        holder.tvStatus.setText("Status: " + item.getStatus());

        // Buttons only visible when pending
        boolean isPending = "pending".equalsIgnoreCase(item.getStatus());
        holder.btnApprove.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.btnReject.setVisibility(isPending ? View.VISIBLE : View.GONE);

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(item));
        holder.btnReject.setOnClickListener(v -> listener.onReject(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvStudent, tvMessage, tvStatus;
        Button btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvRequestType);
            tvStudent = itemView.findViewById(R.id.tvRequestStudent);
            tvMessage = itemView.findViewById(R.id.tvRequestMessage);
            tvStatus = itemView.findViewById(R.id.tvRequestStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
