package com.example.aplikasitravel_uas.admin

import android.app.DatePickerDialog
import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.aplikasitravel_uas.Travel
import com.example.aplikasitravel_uas.databinding.ActivityTravelAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class TravelAdminActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val travelsRefColl = firestore.collection("travels")

    private lateinit var binding : ActivityTravelAdminBinding

    var tujuanPrices = 0
    var asalPrices = 0
    var clasPrices = 0
    var servicesPrices = 0

    companion object {
        const val EXTRA_TRAVEL_ID = "TRAVEL_ID"
    }

    val asal = arrayOf(
        arrayOf("Tawang","20000"),
        arrayOf("Poncol","25000"),
        arrayOf("Tegal","30000"),
        arrayOf("Pekalongan","35000"),
        arrayOf("Ambarawa","40000")
    )

    val tujuan = arrayOf(
        arrayOf("Ampera","20000"),
        arrayOf("Andir","25000"),
        arrayOf("Babakam","30000"),
        arrayOf("Batutulis","35000"),
        arrayOf("Bogor","40000")
    )

    val classes = arrayOf(
        arrayOf("Economic","50000"),
        arrayOf("Business","100000")
    )

    val asalList = getDaerahList(asal)
    val tujuanList = getDaerahList(tujuan)
    val classList = getDaerahList(classes)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTravelAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val travelId = intent.getStringExtra(EXTRA_TRAVEL_ID)
        Log.d("TravelAdminActivity", "Nilai travelId: $travelId")
        with(binding){

            var prices = tujuanPrices + asalPrices + clasPrices + servicesPrices

            if (travelId != null && travelId != "TRAVEL_ID") {
                fetchTravelDataById(travelId)
                btnSubmit.text = "Update Travel"
                setupToggleButtons(true)
            } else setupToggleButtons(false)


            edtSchedule.setOnClickListener(){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(this@TravelAdminActivity, { _, y, m, d ->
                    edtSchedule.setText("$d/${m+1}/$y")
                }, year, month, day)
                datePickerDialog.show()
            }



            val adapterAsal = ArrayAdapter(
                this@TravelAdminActivity, R.layout.simple_spinner_item,asalList
            )

            val adapterTujuan = ArrayAdapter<String>(
                this@TravelAdminActivity, R.layout.simple_spinner_item, tujuanList
            )

            val adapterClasses = ArrayAdapter<String>(
                this@TravelAdminActivity, R.layout.simple_spinner_item, classList
            )


            spinnerStasiunAsal.adapter= adapterAsal
            spinnerStasiunTujuan.adapter = adapterTujuan
            spinnerClass.adapter = adapterClasses




            spinnerStasiunTujuan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedItem = spinnerStasiunTujuan.getItemAtPosition(p2).toString()
                    val price = getSelectedPrice(tujuan, selectedItem)
                    tujuanPrices = price?.toInt() ?: 0 // Use 0 as a default value if price is null
                    prices = tujuanPrices + asalPrices + clasPrices + servicesPrices
                    txtPrices.text = "Rp $prices"

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

            spinnerStasiunAsal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedItem = spinnerStasiunAsal.getItemAtPosition(p2).toString()
                    val price = getSelectedPrice(asal, selectedItem)
                    asalPrices = price?.toInt() ?: 0 // Use 0 as a default value if price is null
                    prices = tujuanPrices + asalPrices + clasPrices + servicesPrices
                    txtPrices.text = "Rp $prices"

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

            spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedItem = spinnerClass.getItemAtPosition(p2).toString()
                    val price = getSelectedPrice(classes, selectedItem)
                    clasPrices = price?.toInt() ?: 0 // Use 0 as a default value if price is null
                    prices = tujuanPrices + asalPrices + clasPrices + servicesPrices
                    txtPrices.text = "Rp $prices"
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }


            btnSubmit.setOnClickListener(){
                val activatedToggleButtons = mutableListOf<String>()

                if (togglbtnBagasi.isChecked) {
                    activatedToggleButtons.add("Bagasi")
                }
                if (togglbtnHeadrest.isChecked) {
                    activatedToggleButtons.add("Headrest")
                }
                if (togglbtnKaraoke.isChecked) {
                    activatedToggleButtons.add("Karaoke")
                }
                if (togglbtnMakan.isChecked) {
                    activatedToggleButtons.add("Makan")
                }
                if (togglbtnKursi.isChecked) {
                    activatedToggleButtons.add("Kursi")
                }
                if (togglbtnMinibar.isChecked) {
                    activatedToggleButtons.add("Minibar")
                }
                if (togglbtnTV.isChecked) {
                    activatedToggleButtons.add("TV")
                }
                if (togglbtnToilet.isChecked) {
                    activatedToggleButtons.add("Toilet")
                }


                val selectedAsal = spinnerStasiunAsal.selectedItem.toString()
                val selectedTujuan = spinnerStasiunTujuan.selectedItem.toString()
                val selectedClass = spinnerClass.selectedItem.toString()
                val selectedDate = edtSchedule.text.toString()
                val totalPrices = tujuanPrices + asalPrices + clasPrices + servicesPrices

                val travel = Travel( tujuan = selectedTujuan, asal = selectedAsal, travelClass = selectedClass, schedule = selectedDate, price = totalPrices, services = activatedToggleButtons)

                if (travelId != null && travelId != "TRAVEL_ID" ) {
                    val updateTravel = Travel( id= travelId, tujuan = selectedTujuan, asal = selectedAsal, travelClass = selectedClass, schedule = selectedDate, price = totalPrices, services = activatedToggleButtons)
                    travelsRefColl.document(travelId!!)
                        .set(updateTravel)
                        .addOnSuccessListener {
                            // Travel berhasil diupdate dengan ID dokumen yang tepat
                            // Lakukan apa yang perlu dilakukan setelah berhasil
                            showToast("Update berhasil")
                            finish()
                        }
                        .addOnFailureListener { e ->
                            showToast("Gagal melakukan update")
                            // Penanganan kesalahan jika penambahan Travel baru gagal
                        }
                } else {
                    travelsRefColl.add(travel)
                        .addOnSuccessListener { documentReference ->
                            val newDocumentId = documentReference.id
                            travel.id = newDocumentId // Menggunakan ID dokumen baru sebagai ID Travel

                            // Tambahkan travel dengan ID baru ke Firestore
                            travelsRefColl.document(newDocumentId)
                                .set(travel)
                                .addOnSuccessListener {
                                    // Travel berhasil ditambahkan dengan ID dokumen yang tepat
                                    // Lakukan apa yang perlu dilakukan setelah berhasil
                                    showToast("Tambah berhasil")
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    // Penanganan kesalahan jika penambahan Travel baru gagal
                                    showToast("Gagal menambah data")

                                }
                        }
                        .addOnFailureListener { e ->
                            // Penanganan kesalahan jika penambahan Travel baru gagal
                            showToast("Gagal menambah data")

                        }
                }



            }

        }
    }

    fun getDaerahList(tujuan: Array<Array<String>>): List<String> {
        val tujuanList = mutableListOf<String>()
        for (i in tujuan.indices) {
            tujuanList.add(tujuan[i][0] as String)
        }
        return tujuanList
    }

    fun getSelectedPrice(array: Array<Array<String>>, target: String): String? {
        for (i in array.indices) {
            if (array[i][0] == target) {
                return array[i][1]
            }
        }
        return null
    }


    // Buat fungsi untuk mengambil data travel dari Firestore berdasarkan ID
    private fun fetchTravelDataById(travelId: String) {
        travelsRefColl.document(travelId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val travel = documentSnapshot.toObject(Travel::class.java)
                    // Gunakan data travel yang diperoleh
                    if (travel != null) {
                        binding.edtSchedule.setText(travel.schedule)
                        binding.spinnerStasiunAsal.setSelection(asalList.indexOf(travel.asal))
                        binding.spinnerStasiunTujuan.setSelection(tujuanList.indexOf(travel.tujuan))
                        binding.spinnerClass.setSelection(classList.indexOf(travel.travelClass))
                        travel.services.forEach { service ->
                            when (service) {
                                "Bagasi" -> binding.togglbtnBagasi.isChecked = true
                                "Headrest" -> binding.togglbtnHeadrest.isChecked = true
                                "Karaoke" -> binding.togglbtnKaraoke.isChecked = true
                                "Makan" -> binding.togglbtnMakan.isChecked = true
                                "Kursi" -> binding.togglbtnKursi.isChecked = true
                                "Minibar" -> binding.togglbtnMinibar.isChecked = true
                                "TV" -> binding.togglbtnTV.isChecked = true
                                "Toilet" -> binding.togglbtnToilet.isChecked = true
                            }

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Penanganan jika gagal mengambil data dari Firestore
            }
    }

    fun showToast(message: String) {
        Toast.makeText(this@TravelAdminActivity, message, Toast.LENGTH_SHORT).show()
    }


    private fun setupToggleButtons(isEditMode: Boolean) {
        var prices = 0

        val toggleButtons = listOf(
            binding.togglbtnBagasi to 50000,
            binding.togglbtnHeadrest to 25000,
            binding.togglbtnKaraoke to 50000,
            binding.togglbtnMakan to 25000,
            binding.togglbtnKursi to 10000,
            binding.togglbtnMinibar to 30000,
            binding.togglbtnTV to 60000,
            binding.togglbtnToilet to 60000
        )

        toggleButtons.forEach { (toggleBtn, price) ->
            toggleBtn.setOnCheckedChangeListener { _, isChecked ->
                // Perbarui harga berdasarkan setiap toggle button yang diubah statusnya
                prices = calculateNewPrice(prices, isChecked, price)
                servicesPrices = prices
                val totalPrices = tujuanPrices + asalPrices + clasPrices + servicesPrices
                // Tampilkan harga terbaru
                binding.txtPricesServices.text = "Rp $prices"
                binding.txtPrices.text = "Rp $totalPrices"

            }
        }

        if (!isEditMode) {
            toggleButtons.forEach { (toggleBtn, price) ->
                toggleBtn.setOnCheckedChangeListener { _, isChecked ->
                    // Perbarui harga berdasarkan setiap toggle button yang diubah statusnya
                    prices = calculateNewPrice(prices, isChecked, price)
                    servicesPrices = prices
                    val totalPrices = tujuanPrices + asalPrices + clasPrices + servicesPrices
                    // Tampilkan harga terbaru
                    binding.txtPricesServices.text = "Rp $prices"
                    binding.txtPrices.text = "Rp $totalPrices"
                }
            }
        }
    }

    private fun calculateNewPrice(currentPrice: Int, isChecked: Boolean, price: Int): Int {
        return if (isChecked) {
            currentPrice + price
        } else {
            currentPrice - price
        }
    }


}