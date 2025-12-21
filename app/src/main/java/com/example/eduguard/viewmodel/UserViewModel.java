package com.example.eduguard.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eduguard.data.model.CreateUserRequest;
import com.example.eduguard.data.model.User;
import com.example.eduguard.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;

    private final MutableLiveData<List<User>> studentsList = new MutableLiveData<>();
    private final MutableLiveData<List<User>> mentorsList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> assignResult = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application.getApplicationContext());
    }

    // FETCH STUDENTS
    public void fetchStudents() {
        repository.fetchStudents(studentsList, errorMessage);
    }

    public LiveData<List<User>> getStudentsList() { return studentsList; }

    // FETCH MENTORS
    public void fetchMentors() {
        repository.fetchMentors(mentorsList, errorMessage);
    }

    public LiveData<List<User>> getMentorsList() { return mentorsList; }

    // CREATE
    public void createUser(String name, String email, String password, String phone, String role, String mentorId) {
        CreateUserRequest req = new CreateUserRequest(name, email, phone, password, role, mentorId);
        repository.createUser(req, createResult, errorMessage);
    }

    public LiveData<Boolean> getCreateResult() { return createResult; }

    // UPDATE
    public void updateUser(String userId, String updatedUser) {
        repository.updateUser(userId, updatedUser, updateResult, errorMessage);
    }

    public LiveData<Boolean> getUpdateResult() { return updateResult; }

    // DELETE
    public void deleteUser(String userId) {
        repository.deleteUser(userId, deleteResult, errorMessage);
    }

    public LiveData<Boolean> getDeleteResult() { return deleteResult; }

    // ASSIGN STUDENT → MENTOR
    public void assignStudent(String studentId, String mentorId) {
        repository.assignStudentToMentor(studentId, mentorId, assignResult, errorMessage);
    }

    public LiveData<Boolean> getAssignResult() { return assignResult; }

    public LiveData<String> getErrorMessage() { return errorMessage; }
}
