package com.example.aplikasitravel_uas

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.aplikasitravel_uas.databinding.FragmentRegisterBinding
import com.example.aplikasitravel_uas.manager.PrefManager
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersRefColl = firestore.collection("users")

    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_register, container, false)
        val main = activity as MainActivity

        val htmlString = "By checking the box you agree to our <a href=\"https://www.example.com/terms\"><b>Terms</b></a> and <a href=\"https://www.example.com/policy\"><b>Policy</b></a>."
        view.findViewById<CheckBox>(R.id.checkBox).text = Html.fromHtml(htmlString)

        val htmlStringLogin = "Already Have an Account? <a href=\"https://www.example.com/terms\"><b>Login</b></a>"
        view.findViewById<TextView>(R.id.txt_login2).text = Html.fromHtml(htmlStringLogin)


        view.findViewById<AppCompatButton>(R.id.btn_Register).setOnClickListener(){
            val username = view.findViewById<EditText>(R.id.edt_username).text.toString()
            val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

            if (checkBox.isChecked){
                checkUsernameAvailability(username)
            } else {
                Toast.makeText(requireContext(), "Anda harus menyetujui FAQ untuk mendaftar", Toast.LENGTH_SHORT).show()
            }


//            main.viewPager.setCurrentItem(1);
        }

        view.findViewById<TextView>(R.id.txt_login2).setOnClickListener(){
            main.viewPager.setCurrentItem(1)
        }
        return view
    }

    private fun checkUsernameAvailability(username: String) {
        usersRefColl.whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Username tersedia, lanjutkan proses registrasi
                    val password = view?.findViewById<EditText>(R.id.edt_password)?.text.toString()
                    val user = User(username = username, password = password, role_id = "1")
                    registerNewUser(user)
                } else {
                    // Username sudah digunakan, beri pesan kepada pengguna
                    Toast.makeText(requireContext(), "Username sudah digunakan, coba username lain", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Check Username", "Failed to check username: $exception")
            }
    }

    private fun registerNewUser(user: User) {
        usersRefColl.add(user)
            .addOnSuccessListener {
                prefManager = PrefManager.getInstance(requireContext())
                prefManager.saveUsername(user.username)
                prefManager.savePassword(user.password)
                prefManager.setLoggedIn(true)
                prefManager.saveRole("1")
                Toast.makeText(requireContext(), "Register Berhasil", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireContext(), AppActivity::class.java)
                startActivity(intent)

        }.addOnFailureListener {
            Log.d("User Register", "Failed to registering new user: $it")
        }
    }


}