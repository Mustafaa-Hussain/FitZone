package com.example.fitzone.retrofit_requists.data_models.register

data class UserData(
    val badges: List<Any>,
    val created_at: String,
    val email: String,
    val friends_username: List<Any>,
    val id: Int,
    val posts: List<Any>,
    val updated_at: String,
    val username: String
)