package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserActivityJoinDao {

    @Insert
    suspend fun insertJoin(join: UserActivityJoin)

    @Query("SELECT COUNT(*) FROM UserActivityJoin WHERE activityId = :activityId")
    fun getParticipantsCount(activityId: Long): Flow<Int>

    /*@Query("""
    SELECT *, 
    (SELECT COUNT(*) FROM UserActivityJoin WHERE activityId = activities.id) AS currentParticipants 
    FROM activities 
    WHERE creatorId != :userId
""")
    fun getParticipants(userId: Long): Flow<List<Activity>> */
}