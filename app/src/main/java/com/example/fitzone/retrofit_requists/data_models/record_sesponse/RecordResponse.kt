package com.example.fitzone.retrofit_requists.data_models.record_sesponse

data class RecordResponse(
    val granted_badges: List<GrantedBadge>,
    val message: Message,
    val success: Boolean
)