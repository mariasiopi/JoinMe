package com.example.joinme.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val username: String = "",
    val email: String = "",
)