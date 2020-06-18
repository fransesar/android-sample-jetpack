package com.example.jetpack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface JetpackDao {
    @Insert
    suspend fun insertAll(vararg jetpack: JetpackModel): List<Long>

    @Insert
    suspend fun insertData(vararg jetpack: JetpackModel)

    @Query("SELECT * FROM jetpackmodel")
    suspend fun getAllData(): List<JetpackModel>

    @Query("SELECT * FROM jetpackmodel WHERE uuid = :jetpackid")
    suspend fun getData(jetpackid: Int): JetpackModel

    @Query("DELETE FROM jetpackmodel")
    suspend fun deleteAllData()

//    @Delete
//    suspend fun deleteAllData(jetpackModel: JetpackModel)
}