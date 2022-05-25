package com.example.fitzone.retrofit_requists.data_models.single_poset_data

data class Comment(
    val content: String,
    val created_at: String,
    val id: Int,
    val post_id: Int,
    val updated_at: String,
    val user_avatar: String,
    val user_id: Int,
    val username: String
)