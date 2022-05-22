package com.example.fitzone.retrofit_requists.data_models.record_sesponse

data class Message(
    val count: Int,
    val created_at: String,
    val duration: Int,
    val exercise_name: String,
    val updated_at: String,
    val user_id: Int
)