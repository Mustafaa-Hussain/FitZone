package com.example.fitzone.retrofit_requists.data_models.exercise_data

data class ExerciseData(
    val created_at: String,
    val description: Any,
    val exp: Int,
    val name: String,
    val updated_at: String,
    val uploader_id: Int,
    val video: String
)