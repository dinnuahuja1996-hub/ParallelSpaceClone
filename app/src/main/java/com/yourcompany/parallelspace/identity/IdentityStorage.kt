package com.yourcompany.parallelspace.identity

import android.content.Context
import com.google.gson.Gson
import com.yourcompany.parallelspace.model.DeviceIdentity

object IdentityStorage {
    private const val PREF_NAME = "identity_prefs"
    private val gson = Gson()

    fun save(context: Context, packageName: String, identity: DeviceIdentity) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(packageName, gson.toJson(identity)).apply()
    }

    fun load(context: Context, packageName: String): DeviceIdentity? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(packageName, null) ?: return null
        return try { gson.fromJson(json, DeviceIdentity::class.java) } catch (e: Exception) { null }
    }

    fun delete(context: Context, packageName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(packageName).apply()
    }

    fun all(context: Context): Map<String, DeviceIdentity> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.all.mapNotNull { (k, v) ->
            try { k to gson.fromJson(v as String, DeviceIdentity::class.java) } catch (e: Exception) { null }
        }.toMap()
    }
}
