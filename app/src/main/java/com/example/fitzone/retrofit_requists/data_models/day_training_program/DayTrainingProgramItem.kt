package com.example.fitzone.retrofit_requists.data_models.day_training_program

data class DayTrainingProgramItem(
    val day: Int,
    val exercise_name: String,
    val order: Int,
    val reps: Int,
    val sets: Int
)