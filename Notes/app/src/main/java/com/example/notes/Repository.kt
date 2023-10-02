package com.example.notes

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val PREFS_CURRENT_URI = "PREFS_CURRENT_URI"
private const val CURRENT_URI_KEY = "CURRENT_URI_KEY"
class Repository {

     fun saveCurrentURI(context: Context, currentURI: String) {
          val prefs = context.getSharedPreferences(PREFS_CURRENT_URI, MODE_PRIVATE)
          prefs.edit().putString(CURRENT_URI_KEY, currentURI).apply()
     }

     fun getCurrentURI(context: Context): String {
          val prefs = context.getSharedPreferences(PREFS_CURRENT_URI, MODE_PRIVATE)
          return prefs.getString(CURRENT_URI_KEY, "").toString()
     }
}