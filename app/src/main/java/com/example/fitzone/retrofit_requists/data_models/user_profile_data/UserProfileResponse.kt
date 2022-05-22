package com.example.fitzone.retrofit_requists.data_models.user_profile_data

data class UserProfileResponse(
    val avatar: String,
    val badges: List<Badge>,
    val created_at: String,
    val email: String,
    val exp: Int,
    val friends_username: List<String>,
    val id: Int,
    val level: Int,
    val posts: List<Post>,
    val role: Int,
    val to_reach_next_level: Int,
    val updated_at: String,
    val username: String
)