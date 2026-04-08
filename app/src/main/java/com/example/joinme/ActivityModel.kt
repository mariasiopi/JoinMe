package com.example.joinme

data class ActivityModel(
    val title: String,
    val date: String,
    var participants: Int,
    val maxParticipants: Int,
    val location: String
)