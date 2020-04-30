package com.example.domag2.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey
    val uid: Int? = null,
    val depotId: Int,
    val categoryId: Int,
    val cos: String = "123"

)