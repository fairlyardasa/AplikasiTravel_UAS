package com.example.aplikasitravel_uas

data class Order (
    var id: String = "", // Id dari order
    var username: String = "", // username
    var idTravel: String = "", // id travel dari item yang dipesan
    var tujuan: String = "", // Tempat tujuan
    var asal: String = "", // Tempat asal
    var travelClass: String = "", // Kelas perjalanan
    var schedule: String = "", // Jadwal keberangkatan
    var services: List<String> = emptyList(), // Layanan yang tersedia
    var price: Int = 0 // Harga
)