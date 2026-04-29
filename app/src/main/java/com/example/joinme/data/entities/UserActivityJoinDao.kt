package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserActivityJoinDao {

    @Insert
    fun insertJoin(join: UserActivityJoin)

}