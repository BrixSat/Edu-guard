package com.example.eduguard.data.remote;

import com.example.eduguard.data.model.CreateUserRequest;
import com.example.eduguard.data.model.ExtraTimeRequest;
import com.example.eduguard.data.model.LoginResponse;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // -------------------------
    // AUTH
    // -------------------------
    @POST("auth/login")
    Call<LoginResponse> login(@Body Map<String, String> body);


    // -------------------------
    // USERS
    // -------------------------

    // Get logged-in user
    @GET("users/me")
    Call<User> getMyProfile(@Header("Authorization") String token);

    // Admin + Mentor
    @GET("users/students")
    Call<List<User>> getStudents(@Header("Authorization") String token);

    // Admin
    @GET("users/mentors")
    Call<List<User>> getMentors(@Header("Authorization") String token);

    // Admin create user
    @POST("users")
    Call<User> createUser(
            @Header("Authorization") String token,
            @Body CreateUserRequest request
    );


    // Admin update user
    @PATCH("users/{userId}")
    Call<User> updateUser(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Body Object updateBody
    );

    // Admin delete user
    @DELETE("users/{userId}")
    Call<Void> deleteUser(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );


    // -------------------------
    // POLICIES
    // -------------------------
    @GET("policies/{studentId}")
    Call<Policy> getPolicy(
            @Header("Authorization") String token,
            @Path("studentId") String studentId
    );

    @POST("policies/{studentId}")
    Call<Policy> updatePolicy(
            @Header("Authorization") String token,
            @Path("studentId") String studentId,
            @Body Policy policy
    );


    // -------------------------
    // REQUESTS
    // -------------------------
    @POST("requests")
    Call<ExtraTimeRequest> postExtraTimeRequest(
            @Header("Authorization") String token,
            @Body ExtraTimeRequest request
    );

    @GET("requests")
    Call<List<ExtraTimeRequest>> getRequests(@Header("Authorization") String token);

    @PATCH("requests/{id}")
    Call<ExtraTimeRequest> updateRequestStatus(
            @Header("Authorization") String token,
            @Path("id") String requestId,
            @Body Map<String, String> body
    );



    // -------------------------
    // LOGS
    // -------------------------
    @POST("logs")
    Call<Void> uploadUsageLog(
            @Header("Authorization") String token,
            @Body Map<String, Object> logBody
    );

    @GET("logs/{studentId}")
    Call<List<Map<String, Object>>> getLogs(
            @Header("Authorization") String token,
            @Path("studentId") String studentId
    );
}
