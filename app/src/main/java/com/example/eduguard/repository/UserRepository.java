package com.example.eduguard.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.CreateUserRequest;
import com.example.eduguard.data.model.User;
import com.example.eduguard.data.remote.ApiService;
import com.example.eduguard.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;
    private final DataStoreManager dataStoreManager;

    public UserRepository(Context context) {
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
        dataStoreManager = DataStoreManager.getInstance(context);
    }

    private String bearer() {
        return "Bearer " + dataStoreManager.getTokenValue();
    }

    // -------------------------------------------------------------------
    // FETCH STUDENTS
    // -------------------------------------------------------------------
    public void fetchStudents(MutableLiveData<List<User>> result,
                              MutableLiveData<String> error) {

        apiService.getStudents(bearer()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) result.postValue(response.body());
                else error.postValue("Failed to load students: " + response.code());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                error.postValue("Error: " + t.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------
    // FETCH MENTORS
    // -------------------------------------------------------------------
    public void fetchMentors(MutableLiveData<List<User>> result,
                             MutableLiveData<String> error) {

        apiService.getMentors(bearer()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) result.postValue(response.body());
                else error.postValue("Failed to load mentors: " + response.code());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                error.postValue("Error: " + t.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------
    // CREATE USER (Student OR Mentor)
    // -------------------------------------------------------------------
    public void createUser(CreateUserRequest request,
                           MutableLiveData<Boolean> result,
                           MutableLiveData<String> error) {

        apiService.createUser(bearer(), request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    result.postValue(true);  // success
                } else {
                    error.postValue("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }


    // -------------------------------------------------------------------
    // UPDATE USER (Edit Mentor / Edit Student)
    // -------------------------------------------------------------------
    public void updateUser(String userId, String updatedUser,
                           MutableLiveData<Boolean> result,
                           MutableLiveData<String> error) {

        apiService.updateUser(bearer(), userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                result.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------
    // DELETE USER
    // -------------------------------------------------------------------
    public void deleteUser(String userId,
                           MutableLiveData<Boolean> result,
                           MutableLiveData<String> error) {

        apiService.deleteUser(bearer(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------
    // ASSIGN STUDENT → MENTOR
    // PATCH /users/:studentId
    // body: { assignedMentor: mentorId }
    // -------------------------------------------------------------------
    public void assignStudentToMentor(String studentId, String mentorId,
                                      MutableLiveData<Boolean> result,
                                      MutableLiveData<String> error) {

        User updateBody = new User();
        updateBody.setAssignedMentor(mentorId);

        apiService.updateUser(bearer(), studentId, updateBody)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        result.postValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }
}
