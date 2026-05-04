package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Insert
@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long
}