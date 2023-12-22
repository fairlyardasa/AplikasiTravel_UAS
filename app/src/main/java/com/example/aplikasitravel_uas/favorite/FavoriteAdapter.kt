package com.example.aplikasitravel_uas.favorite

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasitravel_uas.R
import com.example.aplikasitravel_uas.favorite.Favorite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteAdapter(
    private val context: Context,
    private val favoriteList: List<Favorite>
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val destination: TextView = itemView.findViewById(R.id.card_destination)
        private val travelClass: TextView = itemView.findViewById(R.id.card_class)
        private val schedule: TextView = itemView.findViewById(R.id.card_schedule)
        private val price: TextView = itemView.findViewById(R.id.card_price)
        private val services: TextView = itemView.findViewById(R.id.card_paket)

        fun bind(favorite: Favorite) {
            destination.text = favorite.asal + " - " + favorite.tujuan
            travelClass.text = favorite.travelClass
            schedule.text = favorite.schedule
            price.text= favorite.price
            services.text= favorite.services.joinToString(", ")

            itemView.setOnClickListener {
                showDeleteConfirmationDialog(favorite)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]
        holder.bind(favorite)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    private fun showDeleteConfirmationDialog(favorite: Favorite) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Delete") { dialog, which ->
            deleteFavoriteItem(favorite)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteFavoriteItem(favorite: Favorite) {
        val favoriteRoomDatabase: FavoriteRoomDatabase = FavoriteRoomDatabase.getDatabase(context)
        val favoriteDao: FavoriteDao = favoriteRoomDatabase.favoriteDao()?: error("FavoriteDao is null")

        CoroutineScope(Dispatchers.IO).launch {
            favoriteDao.delete(favorite) // Assuming delete function in FavoriteDao
            withContext(Dispatchers.Main) {
                // Notify the adapter after deletion
                notifyDataSetChanged()
            }
        }
    }
}
