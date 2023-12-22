package com.example.aplikasitravel_uas.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasitravel_uas.R
import com.example.aplikasitravel_uas.Travel
import com.example.aplikasitravel_uas.TravelAdapter
import com.example.aplikasitravel_uas.databinding.ActivityAdminBinding
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class AdminActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val travelsRef = db.collection("travels")
    private val travelsLiveData : MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }
    private lateinit var prefManager: PrefManager

    private lateinit var binding : ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)
        with(binding){

            buttonPesan.setOnClickListener(){
                val intent = Intent(this@AdminActivity, TravelAdminActivity::class.java)
                startActivity(intent)
            }

            txtUsername.text = prefManager.getUsername() + "!"

            recyclerView.layoutManager = LinearLayoutManager(this@AdminActivity)

            buttonLogout.setOnClickListener {
                prefManager.setLoggedIn(false)
                prefManager.clear()
                finish()
            }

        }

        travelsLiveData.observe(this@AdminActivity, Observer { travelsList ->
            val adapter = TravelAdapter(travelsList, prefManager, this)
            binding.recyclerView.adapter = adapter
        })

        fetchDataAndObserveChanges()



    }


    private fun fetchDataAndObserveChanges() {
        travelsRef.get()
            .addOnSuccessListener { documents ->
                val travelsList = mutableListOf<Travel>()
                for (document in documents) {
                    val travel = document.toObject(Travel::class.java)
                    travel.id = document.id
                    travelsList.add(travel)
                }

                val sortedList = travelsList.sortedBy { travel ->
                    val dateParts = travel.schedule.split("/") // Misalnya "22/12/2023"
                    val year = dateParts[2].toInt()
                    val month = dateParts[1].toInt()
                    val day = dateParts[0].toInt()
                    // Format: tahun, bulan, hari
                    year * 10000 + month * 100 + day
                }


                travelsLiveData.postValue(sortedList) // Update LiveData with initial data

                observeTravelChanges() // Start observing changes in Firestore
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "Error fetching data: $exception")
            }
    }

    private fun observeTravelChanges() {
        travelsRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("AdminActivity", "Error listening for travel changes: $error")
                return@addSnapshotListener
            }

            val travelsList = mutableListOf<Travel>()
            for (document in snapshots!!) {
                val travel = document.toObject(Travel::class.java)
                travel.id = document.id
                travelsList.add(travel)
            }

            val sortedList = travelsList.sortedBy { travel ->
                // Ubah format string ke Date jika schedule adalah tanggal
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = dateFormat.parse(travel.schedule)

                date?.time ?: 0L // Sorting berdasarkan millisec dari Date, atau 0 jika ada kesalahan
            }

            travelsLiveData.postValue(sortedList) // Update LiveData with new data
        }
    }



}