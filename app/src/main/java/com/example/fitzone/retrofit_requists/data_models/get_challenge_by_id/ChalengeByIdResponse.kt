package com.example.fitzone.retrofit_requists.data_models.get_challenge_by_id

data class ChalengeByIdResponse(
    val opponent_name: String,
    val opponent_score: Int,
    val state: Int,
    val winner: String
)