package com.example.aplikasitravel_uas

data class Travel(
    var id: String = "", // Id dari travel item
    var tujuan: String = "", // Tempat tujuan
    var asal: String = "", // Tempat asal
    var travelClass: String = "", // Kelas perjalanan
    var schedule: String = "", // Jadwal keberangkatan
    var services: List<String> = emptyList(), // Layanan yang tersedia
    var price: Int = 0 // Harga
)
