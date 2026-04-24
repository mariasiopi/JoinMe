package com.example.joinme.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "activities",
    foreignKeys = [ ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["creatorId"])
    ]
)

data class Activity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val creatorId: Long,
    val date: String = "",
    val time: String = "",
    val maxParticipants: Int = 0,
    val location: String = "",

    @Ignore var currentParticipants: Int = 0
    ){
    // Χρειάζεται αυτός ο constructor για να μην μπερδεύεται η Room
    constructor(id: Long, title: String, creatorId: Long, date: String, time: String, maxParticipants: Int, location: String) :
            this(id, title, creatorId, date, time, maxParticipants, location, 0)
}