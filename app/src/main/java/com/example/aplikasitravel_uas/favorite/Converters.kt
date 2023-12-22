package com.example.aplikasitravel_uas.favorite

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",") // Misalkan pemisahnya adalah koma (kamu dapat menggunakan pemisah yang sesuai)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",") // Konversi List<String> ke String dengan menggunakan pemisah tertentu
    }
}
