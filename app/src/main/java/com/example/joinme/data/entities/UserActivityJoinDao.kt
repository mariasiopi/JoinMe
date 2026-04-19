package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserActivityJoinDao {

    @Insert
    suspend fun insertJoin(join: UserActivityJoin)

    @Query("SELECT COUNT(*) FROM UserActivityJoin WHERE activityId = :activityId")
    fun getParticipantsCount(activityId: Long): Int
}