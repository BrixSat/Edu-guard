package com.example.eduguard.ui.admin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.ExtraTimeRequest;
import com.example.eduguard.data.remote.RequestRepository;
import com.example.eduguard.ui.admin.adapters.RequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestsFragment extends Fragment {

    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private RequestRepository repo;

    private final List<ExtraTimeRequest> requestList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_requests, container, false);

        rvRequests = view.findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        repo = new RequestRepository(getContext());

        adapter = new RequestAdapter(requestList, new RequestAdapter.OnRequestActionListener() {
            @Override
            public void onApprove(ExtraTimeRequest req) {
                update(req, "approved");
            }

            @Override
            public void onReject(ExtraTimeRequest req) {
                update(req, "rejected");
            }
        });

        rvRequests.setAdapter(adapter);

        loadRequests();

        return view;
    }

    private void loadRequests() {
        repo.getAllRequests(new RequestRepository.CallbackList() {
            @Override
            public void onSuccess(List<ExtraTimeRequest> list) {
                requestList.clear();
                requestList.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(ExtraTimeRequest req, String status) {
        repo.updateRequestStatus(req.getId(), status, new RequestRepository.CallbackSingle() {
            @Override
            public void onSuccess(ExtraTimeRequest response) {
                Toast.makeText(getContext(), status.toUpperCase(), Toast.LENGTH_SHORT).show();
                loadRequests();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
