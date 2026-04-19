package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ActivitiesDao {

    @Insert
    suspend fun insertActivity(activity: Activity)

    @Update
    suspend fun updateActivity(activity: Activity)

    @Delete
    suspend fun deleteActivity(activity: Activity)

    //Query για τις δικες μου δραστηριοτητες
    @Query("SELECT * FROM activities WHERE creatorId = :Id")
    fun getActivitiesForUser(Id: Long): List<Activity>

    //Query για τις δραστηριοτητες των αλλων
    @Query("SELECT * FROM activities WHERE creatorId != :Id")
    fun getOthersActivities(Id: Long): List<Activity>
}