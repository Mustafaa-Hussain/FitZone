package com.example.fitzone.retrofit_requists.data_models.badges

data class DataModelBadgesResponseItem(
    val description: String,
    val image: String,
    val motivation: Any,
    val name: String,
    val rules: List<Rule>
)