package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT userId FROM users WHERE username = :username")
    fun getUserIdByName(username: String): Long
}