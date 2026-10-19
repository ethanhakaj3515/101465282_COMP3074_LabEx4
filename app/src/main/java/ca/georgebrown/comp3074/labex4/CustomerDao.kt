package ca.georgebrown.comp3074.labex4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun observeAll(): Flow<List<Customer>>

    @Insert
    suspend fun insert(customer: Customer)

    @Query("DELETE FROM customers WHERE id = :customerId")
    suspend fun deleteById(customerId: Long)
}
