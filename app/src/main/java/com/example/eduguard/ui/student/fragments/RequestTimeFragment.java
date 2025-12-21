package com.example.eduguard.ui.student.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eduguard.R;
import com.example.eduguard.data.model.ExtraTimeRequest;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.data.remote.RequestRepository;
import com.example.eduguard.policy.PolicyStore;

import java.util.ArrayList;
import java.util.List;

public class RequestTimeFragment extends Fragment {

    private Spinner spinnerApps;
    private EditText etMinutes, etReason;
    private Button btnSubmit;

    private RequestRepository repo;
    private final List<String> allowedAppsList = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_request_time, container, false);

        spinnerApps = view.findViewById(R.id.spinnerApps);
        etMinutes = view.findViewById(R.id.etMinutes);
        etReason = view.findViewById(R.id.etReason);
        btnSubmit = view.findViewById(R.id.btnSubmitRequest);

        repo = new RequestRepository(requireContext());

        loadAllowedApps();
        btnSubmit.setOnClickListener(v -> submitRequest());

        return view;
    }

    // ---------------------------------------------------------------
    // Load allowed apps from stored policy on student device
    // ---------------------------------------------------------------
    private void loadAllowedApps() {
        Policy policy = new PolicyStore(requireContext()).getPolicy();

        if (policy == null || policy.getAllowedApps() == null) {
            Toast.makeText(getContext(), "No allowed apps found", Toast.LENGTH_SHORT).show();
            return;
        }

        allowedAppsList.clear();

        for (Policy.AppRule rule : policy.getAllowedApps()) {
            allowedAppsList.add(rule.getPackageName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                allowedAppsList
        );
        spinnerApps.setAdapter(adapter);
    }

    // ---------------------------------------------------------------
    // Submit request to backend
    // ---------------------------------------------------------------
    private void submitRequest() {

        String selectedPackage = (String) spinnerApps.getSelectedItem();
        String minsStr = etMinutes.getText().toString().trim();
        String reasonStr = etReason.getText().toString().trim();

        if (selectedPackage == null) {
            Toast.makeText(getContext(), "Select an app", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(minsStr)) {
            Toast.makeText(getContext(), "Enter minutes", Toast.LENGTH_SHORT).show();
            return;
        }

        int minutes;
        try {
            minutes = Integer.parseInt(minsStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid minutes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Backend expects fields:
        // type, message, requestedForApp, requestedExtraMinutes

        ExtraTimeRequest request = new ExtraTimeRequest();
        request.setType("extraTime");
        request.setRequestedForApp(selectedPackage);
        request.setRequestedExtraMinutes(minutes);
        request.setMessage(reasonStr);

        btnSubmit.setEnabled(false);

        repo.postExtraTimeRequest(request, new RequestRepository.CallbackSingle() {
            @Override
            public void onSuccess(ExtraTimeRequest response) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Request Sent ✔", Toast.LENGTH_SHORT).show();
                    etMinutes.setText("");
                    etReason.setText("");
                    btnSubmit.setEnabled(true);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                    btnSubmit.setEnabled(true);
                });
            }
        });
    }
}
