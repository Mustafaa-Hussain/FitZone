package com.example.fitzone.retrofit_requists.data_models.user_profile_data

data class Post(
    val caption: String,
    val comments: List<Comment>,
    val content: String,
    val created_at: String,
    val id: Int,
    var liked: Boolean,
    val likes: List<String>,
    val number_of_comments: Int,
    var number_of_likes: Int,
    val type: Int,
    val updated_at: String,
    val user_avatar: String,
    val user_id: Int,
    val username: String
) {
    @JvmName("setNumber_of_likes1")
    fun setNumber_of_likes(number_of_likes: Int) {
        this.number_of_likes = number_of_likes
    }

    @JvmName("setNumber_of_likes1")
    fun setLiked(liked: Boolean) {
        this.liked = liked
    }

}