// identity/DeviceSpoofer.kt
package com.yourcompany.parallelspace.identity

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.yourcompany.parallelspace.model.DeviceIdentity
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Runtime mein device APIs ko intercept kare
 * VirtualApp/BlackBox ke through system calls intercept hote hain
 */
class DeviceSpoofer(private val context: Context) {

    /**
     * Sabhi identity values ko runtime mein apply kare
     */
    fun applyAll(identity: DeviceIdentity) {
        applyBuildFields(identity)
        applySettingsSecure(identity)
        applySystemProperties(identity)
        applyGooglePlayServices(identity)
    }

    private fun applyBuildFields(identity: DeviceIdentity) {
        val fields = mapOf(
            "BRAND" to identity.brand,
            "MODEL" to identity.model,
            "MANUFACTURER" to identity.manufacturer,
            "DEVICE" to identity.device,
            "PRODUCT" to identity.product,
            "HARDWARE" to identity.hardware,
            "FINGERPRINT" to identity.fingerprint,
            "SERIAL" to identity.serialNumber,
            "HOST" to identity.hostname,
            "DISPLAY" to identity.display,
            "BOOTLOADER" to identity.bootloader,
            "BOARD" to identity.device.take(6)
        )
        
        for ((name, value) in fields) {
            try {
                val field = Build::class.java.getDeclaredField(name)
                field.isAccessible = true
                val modifiers = Field::class.java.getDeclaredField("accessFlags")
                modifiers.isAccessible = true
                modifiers.setInt(field, field.modifiers and Modifier.FINAL.inv())
                field.set(null, value)
            } catch (_: Exception) { }
        }
    }
    
    private fun applySettingsSecure(identity: DeviceIdentity) {
        try {
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
                identity.androidId
            )
        } catch (_: Exception) { }
    }
    
    private fun applySystemProperties(identity: DeviceIdentity) {
        try {
            System.setProperty("ro.build.fingerprint", identity.fingerprint)
            System.setProperty("ro.serialno", identity.serialNumber)
            System.setProperty("ro.product.model", identity.model)
            System.setProperty("ro.product.manufacturer", identity.manufacturer)
            System.setProperty("ro.product.brand", identity.brand)
            System.setProperty("ro.product.device", identity.device)
            System.setProperty("ro.hardware", identity.hardware)
            System.setProperty("ro.build.display.id", identity.display)
            System.setProperty("persist.sys.timezone", identity.timezone)
        } catch (_: Exception) { }
    }
    
    private fun applyGooglePlayServices(identity: DeviceIdentity) {
        try {
            System.setProperty("ro.com.google.gmsversion", 
                listOf("25_30", "24_45", "23_20", "25_15")[identity.deviceId.length % 4])
        } catch (_: Exception) { }
    }
}
