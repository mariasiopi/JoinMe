package com.example.joinme.data.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivitiesDao {

    @Insert
    fun insertActivity(activity: Activity): Long

    @Delete
    fun deleteActivity(activity: Activity)

    //Query για τις δικές μου δραστηριότητες
    @Query("SELECT * FROM activities WHERE creatorId = :Id")
    fun getMyActivities(Id: Long): Flow<List<Activity>>

    //Query για τις δραστηριότητες των άλλων
    @Query("SELECT * FROM activities WHERE creatorId != :Id")
    fun getOthersActivities(Id: Long): Flow<List<Activity>>

    @Query("""UPDATE activities SET currentParticipants = currentParticipants + 1 
            WHERE id = :activityId AND currentParticipants < maxParticipants""")
    fun incParticipants(activityId: Long)

}