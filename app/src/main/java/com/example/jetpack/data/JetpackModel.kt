package com.example.jetpack.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class JetpackModel(
    @ColumnInfo(name = "data_id")
    @SerializedName("id")
    val jetpackId: String?,

    @ColumnInfo(name = "data_name")
    @SerializedName("name")
    val jetpackName: String?,

    @ColumnInfo(name = "data_span")
    @SerializedName("life_span")
    val jetpackLifespan: String?,

    @ColumnInfo(name = "data_group")
    @SerializedName("breed_group")
    val jetpackGroup: String?,

    @ColumnInfo(name = "data_for")
    @SerializedName("bred_for")
    val jetpackFor: String?,

    @ColumnInfo(name = "data_temperament")
    @SerializedName("temperament")
    val jetpackTemperament: String?,

    @ColumnInfo(name = "data_url")
    @SerializedName("url")
    val jetpackImage: String?
){
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}