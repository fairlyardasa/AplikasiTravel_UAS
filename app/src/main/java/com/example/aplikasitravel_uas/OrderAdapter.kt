package com.example.aplikasitravel_uas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destination: TextView = itemView.findViewById(R.id.card_destination)
        val travelClass: TextView = itemView.findViewById(R.id.card_class)
        val schedule: TextView = itemView.findViewById(R.id.card_schedule)
        val price: TextView = itemView.findViewById(R.id.card_price)
        val services: TextView = itemView.findViewById(R.id.card_paket)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentTravel = orders[position]
        holder.destination.text = "${currentTravel.asal} - ${currentTravel.tujuan}"
        holder.travelClass.text = currentTravel.travelClass
        holder.schedule.text = currentTravel.schedule
        holder.price.text = currentTravel.price.toString()
        holder.services.text = currentTravel.services.joinToString(", ")
    }

    override fun getItemCount() = orders.size
}
