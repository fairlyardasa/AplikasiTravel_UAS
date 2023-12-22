package com.example.aplikasitravel_uas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasitravel_uas.databinding.FragmentHomeBinding
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersColl = firestore.collection("orders")
    private val ordersLiveData : MutableLiveData<List<Order>> by lazy {
        MutableLiveData<List<Order>>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())

        with(binding) {
            txtUsername.text = prefManager.getUsername() + "!"

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            listenToOrderChanges()

        }
    }

    private fun listenToOrderChanges() {
        val ordersListener = ordersColl.whereEqualTo("username", prefManager.getUsername())
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("HomeFragment", "Listen failed: $error")
                    return@addSnapshotListener
                }

                val ordersList = mutableListOf<Order>()

                for (doc in snapshots!!.documents) {
                    val order = doc.toObject(Order::class.java)
                    order?.let { ordersList.add(it) }
                }

                ordersLiveData.value = ordersList
                displayOrders(ordersList)
            }
    }

    private fun displayOrders(ordersList: List<Order>) {
        val adapter = OrderAdapter(ordersList)
        binding.recyclerView.adapter = adapter
    }



}