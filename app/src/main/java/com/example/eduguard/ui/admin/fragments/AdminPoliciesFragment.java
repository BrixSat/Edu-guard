package com.example.eduguard.ui.admin.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.ui.admin.adapters.PolicyAdapter;
import com.example.eduguard.viewmodel.PolicyViewModel;
//import com.google.android.material.materialswitch.MaterialSwitch;

import com.google.android.material.switchmaterial.SwitchMaterial;


import java.util.ArrayList;
import java.util.List;

public class AdminPoliciesFragment extends Fragment {

    private RecyclerView rvApps;
    private SwitchMaterial switchSleepMode;

    private TextView tvStart, tvEnd;
    private EditText etSearch;
    private View btnSave;

    private Policy currentPolicy;
    private PolicyAdapter adapter;

    private final List<Policy.AppRule> masterList = new ArrayList<>();
    private final List<Policy.AppRule> filteredList = new ArrayList<>();

    private PolicyViewModel policyViewModel;
    private DataStoreManager dataStoreManager;

    private String token = "";
    private String loggedInUserId = "";
    private String selectedStudentId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_policies, container, false);

        initViews(view);
        setupDataStore();
        setupViewModel();

        return view;
    }

    private void initViews(View view) {
        rvApps = view.findViewById(R.id.rvPolicyApps);
        switchSleepMode = view.findViewById(R.id.switchSleepMode);
        tvStart = view.findViewById(R.id.tvSleepStart);
        tvEnd = view.findViewById(R.id.tvSleepEnd);
        etSearch = view.findViewById(R.id.etSearchApps);
        btnSave = view.findViewById(R.id.btnSavePolicy);

        rvApps.setLayoutManager(new LinearLayoutManager(requireContext()));
        etSearch.addTextChangedListener(searchWatcher);

        tvStart.setOnClickListener(v -> pickTime(true));
        tvEnd.setOnClickListener(v -> pickTime(false));

        btnSave.setOnClickListener(v -> savePolicyToServer());
    }

    private void setupDataStore() {
        dataStoreManager = DataStoreManager.getInstance(requireContext());

        token = dataStoreManager.getTokenValue();
        if (!token.isEmpty()) token = "Bearer " + token;

        loggedInUserId = dataStoreManager.getUserIdValue();
        if (loggedInUserId == null) loggedInUserId = "";
    }

    private void setupViewModel() {
        policyViewModel = new PolicyViewModel(requireActivity().getApplication());

        if (selectedStudentId.isEmpty()) selectedStudentId = loggedInUserId;

        loadPolicyFromServer();
        observePolicyUpdates();
    }

    private void loadPolicyFromServer() {
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Missing token", Toast.LENGTH_SHORT).show();
            return;
        }

        policyViewModel.getPolicy(selectedStudentId, token);
    }

    private void observePolicyUpdates() {
        policyViewModel.getPolicyResponse().observe(getViewLifecycleOwner(), policy -> {
            if (policy != null) {
                currentPolicy = policy;

                masterList.clear();
                masterList.addAll(policy.getAllowedApps());

                filteredList.clear();
                filteredList.addAll(masterList);

                updateUI();
            }
        });

        policyViewModel.getUpdateStatus().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                Toast.makeText(requireContext(),
                        success ? "Policy Updated ✔" : "Update Failed ❌",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentPolicy == null) return;

        if (currentPolicy.getSleepMode() != null) {
            switchSleepMode.setChecked(currentPolicy.getSleepMode().isEnabled());

            tvStart.setText("Start: " + currentPolicy.getSleepMode().getStartTime());
            tvEnd.setText("End: " + currentPolicy.getSleepMode().getEndTime());
        }

        adapter = new PolicyAdapter(filteredList);
        rvApps.setAdapter(adapter);

        switchSleepMode.setOnCheckedChangeListener((button, enabled) -> {
            currentPolicy.getSleepMode().setEnabled(enabled);
        });
    }

    private void pickTime(boolean isStart) {
        TimePickerDialog tpd = new TimePickerDialog(getContext(),
                (v, hour, minute) -> {
                    String time = String.format("%02d:%02d", hour, minute);

                    if (isStart) {
                        currentPolicy.getSleepMode().setStartTime(time);
                        tvStart.setText("Start: " + time);
                    } else {
                        currentPolicy.getSleepMode().setEndTime(time);
                        tvEnd.setText("End: " + time);
                    }
                },
                22, 0, true
        );

        tpd.show();
    }

    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            filterApps(s.toString());
        }
    };

    private void filterApps(String query) {
        if (adapter == null) return;
        filteredList.clear();

        for (Policy.AppRule rule : masterList) {
            if (rule.getPackageName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(rule);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void savePolicyToServer() {
        policyViewModel.updatePolicy(selectedStudentId, token, currentPolicy);
    }
}
