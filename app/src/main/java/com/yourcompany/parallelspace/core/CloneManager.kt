// core/CloneManager.kt
package com.yourcompany.parallelspace.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.yourcompany.parallelspace.identity.IdentityGenerator
import com.yourcompany.parallelspace.model.AppClone
import com.yourcompany.parallelspace.model.DeviceIdentity
import org.json.JSONArray
import org.json.JSONObject

class CloneManager(private val context: Context) {

    private val identityGenerator = IdentityGenerator()
    private val prefs = context.getSharedPreferences("parallel_clones_db", Context.MODE_PRIVATE)
    
    companion object {
        // Aapki 21 permissions — sab GRANTED
        val GRANTED_PERMISSIONS = listOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.INTERNET",
            "android.permission.VIBRATE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.USE_BIOMETRIC",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.ACCESS_ADSERVICES_ATTRIBUTION",
            "com.google.android.gms.permission.AD_ID",
            "com.google.android.c2dm.permission.RECEIVE",
            "com.android.vending.BILLING",
            "android.permission.USE_FINGERPRINT",
            "com.android.vending.CHECK_LICENSE",
            "android.permission.ACCESS_ADSERVICES_AD_ID",
            "android.permission.ACCESS_ADSERVICES_TOPICS",
            "hk.elftech.block.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
            "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE",
            "hk.elftech.block.permission.RONG_ACCESS_RECEIVER",
            "hk.elftech.block.permission.RONG_BRIDGE_ACTIVITY"
        )
    }

    /**
     * NAYA CLONE BANAYE — Har baar BRAND NEW random identity ke saath
     */
    fun createClone(packageName: String): AppClone {
        val pm = context.packageManager
        
        val appInfo = try { pm.getApplicationInfo(packageName, 0) } catch (e: Exception) { null }
        val appName = pm.getApplicationLabel(appInfo)?.toString() ?: packageName
        val icon = appInfo?.let { pm.getApplicationIcon(it) }
        
        // ====== HAR CLONE KE LIYE FRESH RANDOM IDENTITY ======
        val freshIdentity: DeviceIdentity = identityGenerator.generate()
        
        val cloneNumber = getCloneCount(packageName) + 1
        
        val clone = AppClone(
            id = freshIdentity.deviceId,  // Unique ID
            packageName = packageName,
            appName = "$appName #$cloneNumber",
            icon = icon,
            identity = freshIdentity,
            grantedPermissions = GRANTED_PERMISSIONS,
            pendingPermissions = emptyList(),  // Sab granted = koi pending nahi
            isGoogleService = packageName.startsWith("com.google.") || 
                              packageName.startsWith("com.android."),
            createdAt = System.currentTimeMillis(),
            lastUsed = 0
        )
        
        // Save to storage
        saveClone(clone)
        
        return clone
    }

    /**
     * Saare clones retrieve kare
     */
    fun getAllClones(): List<AppClone> {
        val json = prefs.getString("clones_list", "[]") ?: "[]"
        return parseClonesFromJson(json)
    }

    /**
     * Kisi specific app ke saare clones
     */
    fun getClonesForApp(packageName: String): List<AppClone> {
        return getAllClones().filter { it.packageName == packageName }
    }

    /**
     * Clone launch kare
     */
    fun launchClone(clone: AppClone) {
        // Identity spoofing apply kare
        val spoofer = com.yourcompany.parallelspace.identity.DeviceSpoofer(context)
        spoofer.applyAll(clone.identity)
        
        // App launch
        val intent = context.packageManager.getLaunchIntentForPackage(clone.packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.putExtra("_clone_device_id_", clone.identity.deviceId)
            context.startActivity(it)
        }
        
        // Update last used
        updateLastUsed(clone.id)
    }

    /**
     * Clone delete kare
     */
    fun deleteClone(cloneId: String) {
        val allClones = getAllClones().toMutableList()
        allClones.removeAll { it.id == cloneId }
        saveAllClones(allClones)
    }
    
    private fun getCloneCount(packageName: String): Int {
        return getClonesForApp(packageName).size
    }

    // ========== STORAGE ==========
    
    private fun saveClone(clone: AppClone) {
        val allClones = getAllClones().toMutableList()
        allClones.add(clone)
        saveAllClones(allClones)
    }
    
    private fun saveAllClones(clones: List<AppClone>) {
        val jsonArray = JSONArray()
        for (c in clones) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("packageName", c.packageName)
                put("appName", c.appName)
                put("isGoogleService", c.isGoogleService)
                put("createdAt", c.createdAt)
                put("lastUsed", c.lastUsed)
                put("identity", JSONObject().apply {
                    put("deviceId", c.identity.deviceId)
                    put("androidId", c.identity.androidId)
                    put("advertisingId", c.identity.advertisingId)
                    put("wifiMac", c.identity.wifiMac)
                    put("wifiSsid", c.identity.wifiSsid)
                    put("imei", c.identity.imei)
                    put("brand", c.identity.brand)
                    put("model", c.identity.model)
                    put("manufacturer", c.identity.manufacturer)
                    put("timezone", c.identity.timezone)
                    put("locale", c.identity.locale)
                    put("country", c.identity.country)
                })
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("clones_list", jsonArray.toString()).apply()
    }
    
    private fun parseClonesFromJson(json: String): List<AppClone> {
        // Simplified — in production use proper deserialization
        return emptyList() // Real implementation will parse JSONArray
    }
    
    private fun updateLastUsed(cloneId: String) {
        // Update timestamp
    }
}
