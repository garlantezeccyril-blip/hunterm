package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SignalMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Query("SELECT * FROM signals_memory ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<SignalMemoryEntity>>

    @Query("SELECT * FROM signals_memory WHERE id = :id")
    suspend fun getSignalById(id: Long): SignalMemoryEntity?

    @Query("SELECT * FROM signals_memory WHERE crypto = :crypto ORDER BY timestamp DESC LIMIT 5")
    suspend fun getSignalsForCrypto(crypto: String): List<SignalMemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalMemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignals(signals: List<SignalMemoryEntity>)

    @Update
    suspend fun updateSignal(signal: SignalMemoryEntity)

    @Query("DELETE FROM signals_memory WHERE id = :id")
    suspend fun deleteSignalById(id: Long)

    @Query("SELECT COUNT(*) FROM signals_memory")
    suspend fun getCount(): Int

    @Query("DELETE FROM signals_memory")
    suspend fun clearAll()
}
