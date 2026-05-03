package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserActivityJoinDao {

    @Insert
    fun insertJoin(join: UserActivityJoin)

    /*@Query("SELECT activityId FROM UserActivityJoin WHERE participantId = :participantId")
    fun getJoin(participantId: Long): Flow<List<UserActivityJoinDao>>*/
}