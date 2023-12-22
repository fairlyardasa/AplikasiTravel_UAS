package com.example.aplikasitravel_uas

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.aplikasitravel_uas.admin.AdminActivity
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersRefColl = firestore.collection("users")
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)
        val main = activity as MainActivity
        prefManager = PrefManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.btn_Login).setOnClickListener(){

            val tryUsername = view.findViewById<EditText>(R.id.edt_usernamelogin).text.toString()
            val tryPassword = view.findViewById<EditText>(R.id.edt_passwordlogin).text.toString()
            val checkbox = view.findViewById<CheckBox>(R.id.checkBox2).isChecked

            usersRefColl.whereEqualTo("username", tryUsername)
                .whereEqualTo("password", tryPassword)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result
                        if (documents != null && !documents.isEmpty) {
                            for (document in documents) {
                                val role_id = document.getString("role_id") // Mengambil role_id sebagai string

                                // Memeriksa role_id
                                when (role_id) {
                                    "0" -> {
                                        prefManager.saveUsername(tryUsername)
                                        prefManager.savePassword(tryPassword)
                                        prefManager.saveRole("0")
                                        rememberMeCheckBox(checkbox)
                                        closeActivity()
                                        // Jika role_id adalah "0", intent ke halaman admin
                                        val intentToAdminActivity = Intent(requireContext(), AdminActivity::class.java)
                                        startActivity(intentToAdminActivity)
                                    }
                                    "1" -> {
                                        prefManager.saveUsername(tryUsername)
                                        prefManager.savePassword(tryPassword)
                                        prefManager.saveRole("1")
                                        rememberMeCheckBox(checkbox)
                                        closeActivity()
                                        // Jika role_id adalah "1", intent ke halaman user
                                        val intentToUserActivity = Intent(requireContext(), AppActivity::class.java)
                                        startActivity(intentToUserActivity)
                                    }
                                    else -> {
                                        // Role lainnya, tampilkan pesan kesalahan atau lakukan sesuai kebutuhan
                                        Toast.makeText(requireContext(), "Role tidak valid", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            // Jika tidak ada data yang cocok, tampilkan pesan kesalahan
                            Toast.makeText(requireContext(), "Login Gagal!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle kesalahan jika ada
                        Toast.makeText(requireContext(), "Terjadi kesalahan, coba lagi nanti", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        return view
    }

    fun closeActivity(){
        activity?.finish()
    }

    fun rememberMeCheckBox(boolean: Boolean){
        if (boolean){
            prefManager.setLoggedIn(true)
        }
    }

}