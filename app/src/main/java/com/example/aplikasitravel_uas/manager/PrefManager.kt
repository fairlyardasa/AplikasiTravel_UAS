package com.example.aplikasitravel_uas.manager

import android.content.Context
import android.content.SharedPreferences

class PrefManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_FILENAME = "AuthAppPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val ROLE_ID = "role"
        @Volatile
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: synchronized(this) {
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    init {
        sharedPreferences = context.getSharedPreferences(PREFS_FILENAME,
            Context.MODE_PRIVATE)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }
    fun savePassword(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }
    fun getPassword(): String {
        return sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
    }

    fun saveRole(role:String){
        val editor = sharedPreferences.edit()
        editor.putString(ROLE_ID, role)
        editor.apply()
    }

    fun getRole(): String {
        return sharedPreferences.getString(ROLE_ID, "") ?: ""
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}