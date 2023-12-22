    package com.example.aplikasitravel_uas


    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import androidx.navigation.findNavController
    import androidx.navigation.ui.setupWithNavController
    import com.example.aplikasitravel_uas.databinding.ActivityAppBinding
    import com.example.aplikasitravel_uas.manager.PrefManager

    class AppActivity : AppCompatActivity() {
        private lateinit var binding :ActivityAppBinding
        private lateinit var prefManager: PrefManager


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAppBinding.inflate(layoutInflater)
            setContentView(binding.root)
            prefManager = PrefManager.getInstance(this)
            with(binding) {
                setTitle("TravelApp")
                val navController = findNavController(R.id.nav_host_fragment)
                bottomNavigationView.setupWithNavController(navController)


            }
        }
    }