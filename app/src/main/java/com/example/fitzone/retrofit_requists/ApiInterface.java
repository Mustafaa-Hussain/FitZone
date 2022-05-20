package com.example.fitzone.retrofit_requists;

import com.example.fitzone.retrofit_requists.data_models.badges.DataModelBadgesResponse;
import com.example.fitzone.retrofit_requists.data_models.login.LoginResponse;
import com.example.fitzone.retrofit_requists.data_models.register.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    //register
    @POST("register")
    Call<RegisterResponse> register(@Query("username") String userName,
                                    @Query("email") String email,
                                    @Query("password") String password);

    //login
    @POST("login")
    Call<LoginResponse> login(@Query("email") String email,
                              @Query("password") String password);

    //get badges
    @GET("badges")
    Call<DataModelBadgesResponse> getBadges(@Header("Authorization") String apiToken);
}
