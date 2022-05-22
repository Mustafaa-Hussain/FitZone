package com.example.fitzone.retrofit_requists;

import com.example.fitzone.retrofit_requists.data_models.badges.BadgesResponse;
import com.example.fitzone.retrofit_requists.data_models.day_training_program.DayTrainingProgram;
import com.example.fitzone.retrofit_requists.data_models.exercise_data.ExerciseData;
import com.example.fitzone.retrofit_requists.data_models.login.LoginResponse;
import com.example.fitzone.retrofit_requists.data_models.program_routine_responce.ProgramRoutineResponse;
import com.example.fitzone.retrofit_requists.data_models.programs.ProgramsResponse;
import com.example.fitzone.retrofit_requists.data_models.register.RegisterResponse;

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
    Call<RegisterResponse> register(@Query("username") String userName,
                                    @Query("email") String email,
                                    @Query("password") String password);

    //login
    @POST("login")
    Call<LoginResponse> login(@Query("email") String email,
                              @Query("password") String password);

    //get badges
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
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

}
