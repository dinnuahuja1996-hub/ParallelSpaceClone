// model/DeviceIdentity.kt
package com.yourcompany.parallelspace.model

data class DeviceIdentity(
    // === Device Identifiers ===
    val deviceId: String,            // "35" + 13 random digits (IMEI-style)
    val androidId: String,           // 16-char hex (Settings.Secure.ANDROID_ID)
    val gsfId: String,               // Google Services Framework ID (16-char hex)
    val advertisingId: String,       // Google Advertising ID (UUID format)
    
    // === Network ===
    val wifiMac: String,             // 02:xx:xx:xx:xx:xx
    val bluetoothMac: String,        // Random BT MAC
    val wifiSsid: String,            // Random WiFi name
    val wifiBssid: String,           // Random BSSID
    
    // === Telephony ===
    val imei: String,                // 15-digit with Luhn checksum
    val imsi: String,                // 15-digit (MCC+MNC+MSIN)
    val simSerial: String,           // 19-digit ICCID
    val subscriberId: String,        // IMSI clone
    val phoneNumber: String,         // Random phone
    
    // === Build Properties ===
    val serialNumber: String,        // Build.getSerial()
    val brand: String,               // Build.BRAND
    val model: String,               // Build.MODEL
    val manufacturer: String,        // Build.MANUFACTURER
    val device: String,              // Build.DEVICE
    val product: String,             // Build.PRODUCT
    val hardware: String,            // Build.HARDWARE
    val fingerprint: String,         // Build.FINGERPRINT
    val hostname: String,            // Build.HOST
    val display: String,             // Build.DISPLAY
    val bootloader: String,          // Build.BOOTLOADER
    
    // === Environment ===
    val timezone: String,            // "America/New_York" etc.
    val locale: String,              // "en_US", "en_IN" etc.
    val country: String,             // "US", "IN" etc.
    val kernelVersion: String,       // Kernel version string
    val osVersion: String,           // Android version
    val sdkVersion: Int,             // SDK level
    
    // === Hardware ===
    val screenResolution: String,    // "1080x2400"
    val screenDensity: Int,          // 420, 480, 560
    val totalRam: Long,              // In bytes
    val totalStorage: Long,          // In bytes
    val coreCount: Int,              // CPU cores
    val batteryLevel: Float,         // 0.0 - 1.0
    
    // === Google Accounts ===
    val availableAccounts: List<String>  // Empty = no Google account
)
