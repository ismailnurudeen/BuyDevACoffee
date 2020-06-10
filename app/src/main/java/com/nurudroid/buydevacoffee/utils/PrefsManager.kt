package com.nurudroid.buydevacoffee.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

@SuppressLint("CommitPrefEdits")
class PrefsManager(private val context: Context) {
    private var pref: SharedPreferences
    var defaultPref: SharedPreferences
    var defaultPrefEditor: SharedPreferences.Editor
    private var editor: SharedPreferences.Editor

    // shared pref mode
    private val PRIVATE_MODE = 0

    var userHasDonated: Boolean
        get() = pref.getBoolean(HAS_DONATED, false)
        set(donated) {
            editor.putBoolean(HAS_DONATED, donated)
            editor.commit()
        }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        defaultPref = PreferenceManager.getDefaultSharedPreferences(context)
        defaultPrefEditor = defaultPref.edit()
    }

    companion object {
        // Shared preferences keys
        private const val PREF_NAME = "nurudroid_buydevacoffee_pref"
        private const val HAS_DONATED = "userHasDonated"
    }
}