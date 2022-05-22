package com.example.fitzone.retrofit_requists.data_models.programs

data class ProgramsResponseItem(
    val created_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val type: String,
    val updated_at: String,
    val uploader_id: Int
)