package com.example.aplikasitravel_uas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasitravel_uas.manager.PrefManager


class ProfileFragment : Fragment() {

    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val app = activity as AppActivity
        val btnLogout = view.findViewById<Button>(R.id.btn_Logout)
        prefManager = PrefManager.getInstance(requireContext())

        btnLogout.setOnClickListener {
            prefManager.setLoggedIn(false)
            prefManager.clear()
            activity?.finish()
        }


        return view

    }


}