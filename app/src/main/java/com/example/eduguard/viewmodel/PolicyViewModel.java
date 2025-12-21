package com.example.eduguard.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eduguard.data.model.Policy;
import com.example.eduguard.repository.PolicyRepository;

public class PolicyViewModel extends AndroidViewModel {

    private final PolicyRepository repository;

    private final MutableLiveData<Policy> policyResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PolicyViewModel(@NonNull Application application) {
        super(application);
        repository = new PolicyRepository(application.getApplicationContext());
    }

    // ------------------------------
    // Load policy
    // ------------------------------
    public void getPolicy(String studentId, String tokenIgnored) {
        repository.fetchPolicy(studentId, policyResponse, errorMessage);
    }

    public LiveData<Policy> getPolicyResponse() { return policyResponse; }

    // ------------------------------
    // Update policy
    // ------------------------------
    public void updatePolicy(String studentId, String tokenIgnored, Policy policy) {
        repository.updatePolicy(studentId, policy, updateStatus, errorMessage);
    }

    public LiveData<Boolean> getUpdateStatus() { return updateStatus; }

    public LiveData<String> getErrorMessage() { return errorMessage; }
}
