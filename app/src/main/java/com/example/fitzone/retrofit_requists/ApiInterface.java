package com.example.fitzone.retrofit_requists;

import com.example.fitzone.retrofit_requists.data_models.accept_challenge.AcceptChalengeResponse;
import com.example.fitzone.retrofit_requists.data_models.all_posts.AllPostsResponse;
import com.example.fitzone.retrofit_requists.data_models.all_usernames.AllUsernamesResponse;
import com.example.fitzone.retrofit_requists.data_models.badges.BadgesResponse;
import com.example.fitzone.retrofit_requists.data_models.challenges_response.AllChallengesResponse;
import com.example.fitzone.retrofit_requists.data_models.change_image_profile.ChangeImageProfileResponse;
import com.example.fitzone.retrofit_requists.data_models.crate_chalenge.CreateChallengeResponse;
import com.example.fitzone.retrofit_requists.data_models.day_training_program.DayTrainingProgram;
import com.example.fitzone.retrofit_requists.data_models.exercise_data.ExerciseData;
import com.example.fitzone.retrofit_requists.data_models.get_challenge_by_id.ChalengeByIdResponse;
import com.example.fitzone.retrofit_requists.data_models.increament_reps_by_one.IncrementRepsByOne;
import com.example.fitzone.retrofit_requists.data_models.login.LoginResponse;
import com.example.fitzone.retrofit_requists.data_models.program_routine.ProgramRoutineResponse;
import com.example.fitzone.retrofit_requists.data_models.programs.ProgramsResponse;
import com.example.fitzone.retrofit_requists.data_models.record_sesponse.RecordResponse;
import com.example.fitzone.retrofit_requists.data_models.register.RegisterResponse;
import com.example.fitzone.retrofit_requists.data_models.send_post_response.SendPostResponse;
import com.example.fitzone.retrofit_requists.data_models.single_poset_data.SinglePostResponse;
import com.example.fitzone.retrofit_requists.data_models.user_data.UserDataResponse;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.UserProfileResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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


    //get posts
    @GET("posts/{post_id}")
    Call<SinglePostResponse> getPost(@Header("Authorization") String apiToken, @Path("post_id") int postId);

    //change image profile request
    @Multipart
    @POST("update")
    Call<ChangeImageProfileResponse> changeImageProfile(@Header("Authorization") String apiToken,
                                                        @Part MultipartBody.Part file);

    //send text post
    @POST("posts")
    Call<SendPostResponse> sendTextPost(@Header("Authorization") String apiToken,
                                        @Query("caption") String caption,
                                        @Query("content") String content,
                                        @Query("type") int type);

    //send image | video post
    @Multipart
    @POST("posts")
    Call<SendPostResponse> sendFilePost(@Header("Authorization") String apiToken,
                                        @Query("caption") String caption,
                                        @Part MultipartBody.Part file,
                                        @Query("type") int type);

    //get Competitions
    @GET("challenges")
    Call<AllChallengesResponse> getChallenges(@Header("Authorization") String apiToken);

    //get Competitions
    @GET("challenges/{challenge_id}")
    Call<ChalengeByIdResponse> getChallengeById(@Header("Authorization") String apiToken,
                                                @Path("challenge_id") int challenge_id);

    //accept challenge
    @POST("challenges/{challenge_id}")
    Call<AcceptChalengeResponse> acceptChallenge(@Header("Authorization") String apiToken,
                                                 @Path("challenge_id") int challenge_id);

    //create new challenge
    @POST("challenges")
    Call<CreateChallengeResponse> createChallenge(@Header("Authorization") String apiToken,
                                                  @Query("exercise_name") String exerciseName,
                                                  @Query("reps") int reps);

    //increment challenges reps by one
    @POST("challenges/{challenge_id}/increment")
    Call<IncrementRepsByOne> incrementRepsByOne(@Header("Authorization") String apiToken,
                                                @Path("challenge_id") int challenge_id);
}
