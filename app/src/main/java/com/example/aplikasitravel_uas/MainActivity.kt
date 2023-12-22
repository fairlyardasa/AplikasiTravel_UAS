package com.example.aplikasitravel_uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.aplikasitravel_uas.admin.AdminActivity
import com.example.aplikasitravel_uas.databinding.ActivityMainBinding
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout : TabLayout
    lateinit var viewPager: ViewPager2
    private lateinit var binding : ActivityMainBinding
    private lateinit var prefManager: PrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefManager = PrefManager.getInstance(this)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = TabAdapter(this@MainActivity, tabLayout.tabCount)
        viewPager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // You can set tab text or custom views here
            when (position) {
                0 -> tab.text = "Register"
                1 -> tab.text = "Login"
                else -> throw IllegalArgumentException("Invalid tab position: $position")
            }
        }.attach()

        if (prefManager.isLoggedIn()){
            val intentUser = Intent(this, AppActivity::class.java)
            val intentAdmin = Intent(this, AdminActivity::class.java)

            if (prefManager.getRole() == "0"){
                startActivity(intentAdmin)
                finish()
            } else {
                startActivity(intentUser)
                finish()
            }

        }
    }
}