package com.example.aplikasitravel_uas.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull

@Entity(tableName = "favorite_table")
@TypeConverters(Converters::class)
data class Favorite(

    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Int = 0,

    @ColumnInfo(name = "idTravel")
    val idTravel: String,

    @ColumnInfo(name = "tujuan")
    val tujuan: String,

    @ColumnInfo(name = "asal")
    val asal : String,

    @ColumnInfo(name = "travelClass")
    val travelClass : String,

    @ColumnInfo(name = "schedule")
    val schedule : String,

    @ColumnInfo(name = "services")
    val services : List<String> = ArrayList(),

    @ColumnInfo(name = "price")
    val price : String,

)
