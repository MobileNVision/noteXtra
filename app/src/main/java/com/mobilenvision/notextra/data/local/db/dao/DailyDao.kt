package com.mobilenvision.notextra.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mobilenvision.notextra.data.model.db.Daily

@Dao
interface DailyDao {
    @Delete
    fun deleteDaily(daily: Daily)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDaily(daily: Daily)

    @Update
    fun updateDaily(daily: Daily)

    @Query("SELECT * FROM daily")
    fun getAllDaily(): List<Daily>

    @Query("SELECT * FROM daily WHERE id = :dailyId")
    fun loadDailyById(dailyId: String): Daily

    @Query("SELECT * FROM daily WHERE isSynchronized = 'false'")
    fun getUnSynchronizedDaily(): List<Daily>

    @Query("UPDATE daily SET isSynchronized = :isSynchronized WHERE id = :dailyId")
    fun updateIsSynchronized(dailyId: String, isSynchronized: Boolean)

    @Query("SELECT * FROM daily WHERE day = :day")
    fun loadDailyByDay(day: String): Daily

    @Query("SELECT COUNT(*) FROM daily WHERE day = :day")
    fun checkDailyExists(day: String): Int
}
