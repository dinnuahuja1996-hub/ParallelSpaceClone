// core/PermissionManager.kt
package com.yourcompany.parallelspace.core

import android.content.Context
import android.content.pm.PackageManager
import com.yourcompany.parallelspace.model.AppPermission

class PermissionManager(private val context: Context) {

    companion object {
        val PERMISSION_LABELS = mapOf(
            "android.permission.ACCESS_FINE_LOCATION" to "Precise Location (GPS)",
            "android.permission.ACCESS_COARSE_LOCATION" to "Approximate Location",
            "android.permission.INTERNET" to "Full Network Access",
            "android.permission.VIBRATE" to "Vibrate",
            "android.permission.ACCESS_NETWORK_STATE" to "Network State",
            "android.permission.WAKE_LOCK" to "Prevent Phone From Sleeping",
            "android.permission.USE_BIOMETRIC" to "Use Biometric Hardware",
            "android.permission.ACCESS_WIFI_STATE" to "Wi-Fi Connection Info",
            "android.permission.FOREGROUND_SERVICE" to "Run Foreground Service",
            "android.permission.ACCESS_ADSERVICES_ATTRIBUTION" to "Ad Attribution",
            "com.google.android.gms.permission.AD_ID" to "Advertising ID",
            "com.google.android.c2dm.permission.RECEIVE" to "Push Notifications",
            "com.android.vending.BILLING" to "In-App Purchases",
            "android.permission.USE_FINGERPRINT" to "Fingerprint",
            "com.android.vending.CHECK_LICENSE" to "License Verification",
            "android.permission.ACCESS_ADSERVICES_AD_ID" to "Ad ID Access",
            "android.permission.ACCESS_ADSERVICES_TOPICS" to "Ad Topics",
            "hk.elftech.block.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION" to "Dynamic Broadcast",
            "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE" to "Install Referrer",
            "hk.elftech.block.permission.RONG_ACCESS_RECEIVER" to "Rong Receiver",
            "hk.elftech.block.permission.RONG_BRIDGE_ACTIVITY" to "Rong Bridge"
        )
        
        val PERMISSION_GROUPS = mapOf(
            "android.permission.ACCESS_FINE_LOCATION" to "Location",
            "android.permission.ACCESS_COARSE_LOCATION" to "Location",
            "android.permission.INTERNET" to "Network",
            "android.permission.ACCESS_NETWORK_STATE" to "Network",
            "android.permission.ACCESS_WIFI_STATE" to "Network",
            "android.permission.VIBRATE" to "Device",
            "android.permission.WAKE_LOCK" to "Device",
            "android.permission.USE_BIOMETRIC" to "Biometric",
            "android.permission.USE_FINGERPRINT" to "Biometric",
            "android.permission.FOREGROUND_SERVICE" to "Background",
            "android.permission.ACCESS_ADSERVICES_ATTRIBUTION" to "Advertising",
            "android.permission.ACCESS_ADSERVICES_AD_ID" to "Advertising",
            "android.permission.ACCESS_ADSERVICES_TOPICS" to "Advertising",
            "com.google.android.gms.permission.AD_ID" to "Advertising",
            "com.google.android.c2dm.permission.RECEIVE" to "Google Services",
            "com.android.vending.BILLING" to "Google Play",
            "com.android.vending.CHECK_LICENSE" to "Google Play",
            "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE" to "Google Play",
            "hk.elftech.block.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION" to "System",
            "hk.elftech.block.permission.RONG_ACCESS_RECEIVER" to "System",
            "hk.elftech.block.permission.RONG_BRIDGE_ACTIVITY" to "System"
        )
    }

    /**
     * Sab 21 permissions — sab GRANTED dikhe
     */
    fun getAllPermissions(packageName: String): List<AppPermission> {
        return CloneManager.GRANTED_PERMISSIONS.map { permName ->
            AppPermission(
                name = permName,
                label = PERMISSION_LABELS[permName] ?: permName,
                isGranted = true,  // ** SAB GRANTED **
                group = PERMISSION_GROUPS[permName] ?: "Other",
                protectionLevel = when {
                    permName.contains("LOCATION") || permName.contains("BIOMETRIC") || permName.contains("FINGERPRINT") -> "Dangerous"
                    permName.contains("INTERNET") || permName.contains("NETWORK") -> "Normal"
                    permName.startsWith("com.") -> "Signature"
                    else -> "Normal"
                }
            )
        }
    }
    
    /**
     * Permission count (granted / total)
     */
    fun getPermissionCount(): Pair<Int, Int> {
        return CloneManager.GRANTED_PERMISSIONS.size to CloneManager.GRANTED_PERMISSIONS.size
    }
}
