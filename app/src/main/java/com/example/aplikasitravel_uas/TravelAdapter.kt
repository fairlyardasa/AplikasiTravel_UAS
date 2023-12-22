package com.example.aplikasitravel_uas
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasitravel_uas.admin.TravelAdminActivity
import com.example.aplikasitravel_uas.favorite.Favorite
import com.example.aplikasitravel_uas.favorite.FavoriteDao
import com.example.aplikasitravel_uas.favorite.FavoriteRoomDatabase
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TravelAdapter(
    private val travels: List<Travel>,
    private val prefManager: PrefManager,
    private val context: Context) :
    RecyclerView.Adapter<TravelAdapter.TravelViewHolder>() {

    private val favoriteRoomDatabase: FavoriteRoomDatabase = FavoriteRoomDatabase.getDatabase(context)
    private val favoriteDao: FavoriteDao = favoriteRoomDatabase.favoriteDao()?: error("FavoriteDao is null")
    private val PERMISSION_REQUEST_CODE = 1001
    private val NOTIFICATION_CHANNEL_ID = "TravelOrders"
    private val NOTIFICATION_ID = 1001



    inner class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardDesination : TextView = itemView.findViewById(R.id.card_destination)
        val cardClass : TextView = itemView.findViewById(R.id.card_class)
        val cardSchedule : TextView = itemView.findViewById(R.id.card_schedule)
        val cardPrice : TextView = itemView.findViewById(R.id.card_price)
        val cardPaket : TextView = itemView.findViewById(R.id.card_paket)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TravelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        val currentTravel = travels[position]
        holder.cardDesination.text = currentTravel.asal + " - " + currentTravel.tujuan
        holder.cardClass.text = currentTravel.travelClass
        holder.cardSchedule.text = currentTravel.schedule
        holder.cardPrice.text = "Rp " + currentTravel.price.toString()
        holder.cardPaket.text = currentTravel.services.joinToString(", ")


        holder.itemView.setOnClickListener {
            val options = if (prefManager.getRole() == "0") {
                arrayOf("Edit", "Delete")
            } else {
                arrayOf("Pesan Travel", "Tambahkan ke Favorit")
            }
            // Tampilkan dialog ketika item diklik
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Choose an option")
                .setItems(options) { _, which ->
                    if (prefManager.getRole() == "0"){
                        when (which) {
                            0 -> {
                                // Pilihan Edit: Intent ke TravelAdminActivity
                                val intent = Intent(holder.itemView.context, TravelAdminActivity::class.java)
                                intent.putExtra(TravelAdminActivity.EXTRA_TRAVEL_ID, currentTravel.id) // Kirim ID ke halaman edit
                                Log.d("TravelEXTRAID", "Current Travel ID: ${currentTravel.id}")
                                holder.itemView.context.startActivity(intent)
                            }
                            1 -> {
                                // Pilihan Delete: Hapus dari RecyclerView dan Firebase
                                val docId = currentTravel.id
                                val db = FirebaseFirestore.getInstance()
                                val travelRef = db.collection("travels").document(docId)
                                travelRef.delete()
                                    .addOnSuccessListener {
                                        travels.toMutableList().remove(currentTravel)
                                        notifyItemRemoved(position)
                                        notifyDataSetChanged()
                                        Toast.makeText(holder.itemView.context, "Item deleted", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        // Handle kegagalan penghapusan
                                    }
                            }
                        }
                    } else{
                        when (which) {
                            0 -> {
                                val firestore = FirebaseFirestore.getInstance()
                                val ordersColl = firestore.collection("orders")

                                // Ganti dengan nilai username yang sesuai
                                val username = prefManager.getUsername()

                                // Simpan order ke dalam koleksi "orders" di Firestore
                                val order = Order(
                                    id = "", // Kosongkan ID agar Firestore yang menghasilkan ID otomatis
                                    username = username,
                                    idTravel = currentTravel.id,
                                    tujuan = currentTravel.tujuan,
                                    asal = currentTravel.asal,
                                    schedule = currentTravel.schedule,
                                    travelClass = currentTravel.travelClass,
                                    services = currentTravel.services,
                                    price = currentTravel.price
                                )

                                ordersColl.add(order)
                                    .addOnSuccessListener { documentReference ->
                                        val newOrderId = documentReference.id // Ambil ID dari dokumen yang baru ditambahkan
                                        val message = "Travel " + order.asal + " - " + order.tujuan + " berhasil dipesan!"
                                        order.id = newOrderId


                                        ordersColl.document(newOrderId)
                                            .set(order)
                                            .addOnSuccessListener {
                                                // Travel berhasil ditambahkan dengan ID dokumen yang tepat
                                                // Lakukan apa yang perlu dilakukan setelah berhasil
                                                showToast("Tambah berhasil")
                                                sendNotification(message)
                                            }
                                            .addOnFailureListener { e ->
                                                // Penanganan kesalahan jika penambahan Travel baru gagal
                                                showToast("Gagal menambah data")

                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error adding order", e)
                                        // Handle kesalahan saat menyimpan order
                                    }
                            }
                            1 -> {
                                addToFavorites(currentTravel)
                                showToast("Added to Favorites")
                            }
                        }
                    }
                }
            builder.create().show()
        }
    }


    override fun getItemCount() = travels.size

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun addToFavorites(currentTravel: Travel) {
        CoroutineScope(Dispatchers.IO).launch {
            val favorite = Favorite(
                idTravel = currentTravel.id,
                tujuan = currentTravel.tujuan,
                asal = currentTravel.asal,
                travelClass = currentTravel.travelClass,
                schedule = currentTravel.schedule,
                services = currentTravel.services,
                price = currentTravel.price.toString()
            )

            favoriteDao.insert(favorite)
        }
    }

    private fun sendNotification(message: String) {
        if (ContextCompat.checkSelfPermission(context,"android.permission.ACCESS_NOTIFICATION_POLICY")
            == PackageManager.PERMISSION_GRANTED) {
            createNotificationChannel()

            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Order Travel Berhasil")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            // Jika izin belum diberikan, minta izin kepada pengguna
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf("android.permission.ACCESS_NOTIFICATION_POLICY"),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendNotification("Pesanan berhasil ditambahkan")
                } else {
                    // Izin ditolak, berikan pesan kepada pengguna tentang alasan mengapa izin diperlukan
                    showToast("Izin diperlukan untuk menampilkan notifikasi")
                }
                return
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Travel Orders"
            val descriptionText = "Channel for travel order notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


}
