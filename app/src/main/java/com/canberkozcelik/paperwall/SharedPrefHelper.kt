package com.canberkozcelik.paperwall

/**
 * Created by canberkozcelik on 27.03.2019.
 */
import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(currentContext: Context) {

    private var sharedStringCode = currentContext.applicationContext.packageName + "_shared_private_preference"
    private val sharedPref: SharedPreferences =
        currentContext.applicationContext.getSharedPreferences(sharedStringCode, Context.MODE_PRIVATE)
    private var mEditor: SharedPreferences.Editor? = sharedPref.edit()

    fun putData(key: String, value: Int) {
        mEditor!!.putInt(key, value)
        mEditor!!.apply()
    }

    fun getDataInt(key: String, defaultInt: Int): Int {
        return sharedPref.getInt(key, defaultInt)
    }
}