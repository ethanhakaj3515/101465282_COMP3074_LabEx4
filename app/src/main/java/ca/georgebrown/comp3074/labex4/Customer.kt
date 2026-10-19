package ca.georgebrown.comp3074.labex4

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val isActive: Boolean
)
