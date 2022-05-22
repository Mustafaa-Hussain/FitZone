package com.example.fitzone.retrofit_requists;

import com.example.fitzone.retrofit_requists.data_models.all_posts.AllPostsResponse;
import com.example.fitzone.retrofit_requists.data_models.all_usernames.AllUsernamesResponse;
import com.example.fitzone.retrofit_requists.data_models.badges.BadgesResponse;
import com.example.fitzone.retrofit_requists.data_models.day_training_program.DayTrainingProgram;
import com.example.fitzone.retrofit_requists.data_models.exercise_data.ExerciseData;
import com.example.fitzone.retrofit_requists.data_models.login.LoginResponse;
import com.example.fitzone.retrofit_requists.data_models.program_routine.ProgramRoutineResponse;
import com.example.fitzone.retrofit_requists.data_models.programs.ProgramsResponse;
import com.example.fitzone.retrofit_requists.data_models.record_sesponse.RecordResponse;
import com.example.fitzone.retrofit_requists.data_models.register.RegisterResponse;
import com.example.fitzone.retrofit_requists.data_models.register.UserData;
import com.example.fitzone.retrofit_requists.data_models.user_data.UserDataResponse;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.UserProfileResponse;

import kotlin.PublishedApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    //register
    @POST("register")
    Call<RegisterResponse> register(@Query("username") String username,
                                    @Query("email") String email,
                                    @Query("password") String password);

    //login
    @POST("login")
    Call<LoginResponse> login(@Query("email") String email,
                              @Query("password") String password);

    //get badges
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("badges")
    Call<BadgesResponse> getBadges(@Header("Authorization") String apiToken);

    //get programs
    @GET("programs")
    Call<ProgramsResponse> getPrograms(@Header("Authorization") String apiToken);

    //get the routine of specific program
    @GET("programs/{programName}")
    Call<ProgramRoutineResponse> getProgramRoutine(@Header("Authorization") String apiToken,
                                                   @Path("programName") String programName);

    //get the routine of a specific day of a specific program
    @GET("programs/{programName}/{day}")
    Call<DayTrainingProgram> getDayRoutineOfProgram(@Header("Authorization") String apiToken,
                                                    @Path("programName") String programName,
                                                    @Path("day") int dayNumber);

    //get exercise data for specific exercise name
    @GET("exercises/{exercise_name}")
    Call<ExerciseData> getExerciseData(@Header("Authorization") String apiToken,
                                       @Path("exercise_name") String exerciseName);

    //send finished training data
    @POST("records/exercises")
    Call<RecordResponse> sendFinishedTrainingData(@Header("Authorization") String apiToken,
                                                  @Query("exercise_name") String exerciseName,
                                                  @Query("count") int count,
                                                  @Query("duration") int duration);

    //get all usernames
    @GET("users")
    Call<AllUsernamesResponse> getAllUsernames(@Header("Authorization") String apiToken);

    //get data for specific user
    @GET("users/{username}")
    Call<UserDataResponse> getUserData(@Header("Authorization") String apiToken,
                                       @Path("username") String username);

    //get user profile
    @GET("users/profile")
    Call<UserProfileResponse> getUserProfileData(@Header("Authorization") String apiToken);

    //get posts
    @GET("posts")
    Call<AllPostsResponse> getAllPosts(@Header("Authorization") String apiToken);
}
