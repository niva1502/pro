package com.krishna.pro

import android.content.Context

object SharedPreferencesManager {
    private const val PREFS_NAME = "MyAppPrefs"
    private const val PROFILE_COMPLETED_KEY = "profile_completed"

    fun isProfileCompleted(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PROFILE_COMPLETED_KEY, false)
    }

    fun setProfileCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(PROFILE_COMPLETED_KEY, true)
        editor.apply()
    }
}

