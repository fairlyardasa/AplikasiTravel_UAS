package com.example.aplikasitravel_uas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasitravel_uas.favorite.FavoriteAdapter
import com.example.aplikasitravel_uas.favorite.FavoriteDao
import com.example.aplikasitravel_uas.favorite.FavoriteRoomDatabase
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OrderFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val travelsRef = db.collection("travels")
    private val travelsLiveData : MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }
    private lateinit var prefManager: PrefManager
    private lateinit var mFavoriteDao: FavoriteDao
    private lateinit var executorService: ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_order, container, false)
        val app = activity as AppActivity
        prefManager = PrefManager.getInstance(requireContext())
        executorService = Executors.newSingleThreadExecutor()
        val db = FavoriteRoomDatabase.getDatabase(requireContext())
        mFavoriteDao = db!!.favoriteDao()!!

        val recyclerview = view.findViewById<RecyclerView>(R.id.recycler_view)
        val recyclerFav = view.findViewById<RecyclerView>(R.id.recycler_fav)

        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        // Setelah memperoleh data favorit dari database, tambahkan kode berikut:
        val favoriteList = mFavoriteDao.getAllFavorite()



        favoriteList.observe(viewLifecycleOwner, Observer { favorites ->
            // Update RecyclerView atau tampilan UI lainnya dengan data favorit

            val adapter = FavoriteAdapter(requireContext(), favorites)
            recyclerFav.adapter = adapter
        })


        travelsLiveData.observe(requireActivity(), Observer { travelsList ->
            val adapter = TravelAdapter(travelsList, prefManager, requireContext())
            recyclerview.adapter = adapter
        })

        fetchDataAndObserveChanges()

        return view
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ...

        lifecycleScope.launch {
            val favoriteList = withContext(Dispatchers.IO) {
                mFavoriteDao.getAllFavorite()
            }

            favoriteList.observe(viewLifecycleOwner, Observer { favorites ->
                // Update RecyclerView atau tampilan UI lainnya dengan data favorit
                val recyclerFav = view.findViewById<RecyclerView>(R.id.recycler_fav)
                val favoritesTxt =view.findViewById<TextView>(R.id.textView3)
                val recyclerview = view.findViewById<RecyclerView>(R.id.recycler_view)
                recyclerFav.layoutManager = LinearLayoutManager(requireContext())

                val adapter = FavoriteAdapter(requireContext(), favorites)
                recyclerFav.adapter = adapter

                if (recyclerFav.adapter != null && recyclerFav.adapter?.itemCount ?: 0 > 0){
                    favoritesTxt.visibility = View.VISIBLE
                    recyclerFav.visibility = View.VISIBLE
                    val layoutParams: ViewGroup.LayoutParams = recyclerview.layoutParams
                    layoutParams.height = 1200
                    recyclerview.layoutParams = layoutParams
                } else {
                    favoritesTxt.visibility = View.GONE
                    recyclerFav.visibility = View.GONE
                    val layoutParams: ViewGroup.LayoutParams = recyclerview.layoutParams
                    layoutParams.height = 0
                    recyclerview.layoutParams = layoutParams
                }
            })
        }

    }


}