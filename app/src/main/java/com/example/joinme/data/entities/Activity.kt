package com.example.joinme.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
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
    var currentParticipants: Int = 0
    )