package com.example.fitzone.retrofit_requists.data_models.challenges_response

data class Challenge(
    val created_at: String,
    val creator_name: String,
    val exercise_name: String,
    val id: Int,
    val player_one_id: Int,
    val player_one_score: Int,
    val player_two_id: Any,
    val player_two_score: Int,
    val reps: Int,
    val state: Int,
    val updated_at: String,
    val user_avatar: String,
    val winner_username: Any
)