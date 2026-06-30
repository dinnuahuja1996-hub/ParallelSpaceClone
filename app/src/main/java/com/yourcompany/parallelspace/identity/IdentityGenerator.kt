// identity/IdentityGenerator.kt
package com.yourcompany.parallelspace.identity

import com.yourcompany.parallelspace.model.DeviceIdentity
import java.security.SecureRandom
import java.util.UUID
import kotlin.math.abs

class IdentityGenerator {

    private val random = SecureRandom()

    companion object {
        private val BRANDS = listOf("Google", "Samsung", "Xiaomi", "OnePlus", "OPPO", "vivo", "Motorola", "Nothing", "Sony", "Asus")
        
        private val MODELS = listOf(
            "Pixel 9 Pro" to "Google",
            "Pixel 9" to "Google",
            "Pixel 9 Pro XL" to "Google",
            "Galaxy S25 Ultra" to "Samsung",
            "Galaxy S25+" to "Samsung",
            "Galaxy S25" to "Samsung",
            "Galaxy Z Fold 7" to "Samsung",
            "Galaxy Z Flip 7" to "Samsung",
            "Mi 14 Pro" to "Xiaomi",
            "Redmi Note 14 Pro" to "Xiaomi",
            "POCO X7 Pro" to "Xiaomi",
            "OnePlus 13" to "OnePlus",
            "OnePlus 13R" to "OnePlus",
            "Find X8 Pro" to "OPPO",
            "Reno 13 Pro" to "OPPO",
            "vivo X200 Pro" to "vivo",
            "iQOO 13" to "vivo",
            "Edge 50 Ultra" to "Motorola",
            "Moto G85" to "Motorola",
            "Phone (3a)" to "Nothing",
            "Phone (2a)" to "Nothing",
            "Xperia 1 VII" to "Sony",
            "ROG Phone 9" to "Asus",
            "Zenfone 12" to "Asus"
        )
        
        private val ANDROID_VERSIONS = listOf("15", "14", "13")
        private val SDK_VERSIONS = mapOf("15" to 35, "14" to 34, "13" to 33)
        
        private val TIMEZONES = listOf(
            "America/New_York", "America/Chicago", "America/Los_Angeles",
            "Asia/Kolkata", "Asia/Tokyo", "Asia/Seoul", "Asia/Dubai",
            "Europe/London", "Europe/Berlin", "Europe/Paris",
            "Australia/Sydney", "Brazil/East", "Africa/Cairo"
        )
        
        private val LOCALES = listOf("en_US", "en_IN", "en_GB", "ja_JP", "ko_KR", "de_DE", "fr_FR", "pt_BR", "ar_AE")
        private val COUNTRIES = listOf("US", "IN", "GB", "JP", "KR", "DE", "FR", "BR", "AE", "AU")
        
        private val RESOLUTIONS = listOf(
            "1080x2400", "1080x2340", "1440x3120", 
            "1260x2800", "1080x2412", "1440x3088",
            "1080x2460", "1224x2700"
        )
        
        private val DENSITIES = listOf(420, 440, 480, 560, 640, 400)
        private val RAM_OPTIONS = listOf(6L, 8L, 12L, 16L, 24L)
        private val STORAGE_OPTIONS = listOf(128L, 256L, 512L, 1000L)
        private val CORE_OPTIONS = listOf(8, 8, 10, 12)
        private val KERNEL_VERSIONS = listOf(
            "5.15.123-android13-8-gfe12345abcde",
            "5.10.198-android12-9-ga987654321ab",
            "6.1.49-android14-5-gb12345678901",
            "5.15.148-android14-8-gc12345defab",
            "6.6.30-android15-3-gd98765abcdef"
        )
        private val HARDWARE_OPTIONS = listOf("qcom", "exynos", "tensor", "mt6897", "mt6989", "exynos2500")
        private val BOOTLOADER_OPTIONS = listOf("slider-1.0", "taro-1.1", "zuma-2.0", "pineapple-1.0", "garage-1.0")
        private val SSID_PREFIXES = listOf("HOME", "WiFi", "NET", "Fiber", "Cable", "Family", "Office", "IoT")
    }

    /**
     * Har call par COMPLETELY NEW random identity generate karta hai
     */
    fun generate(): DeviceIdentity {
        val androidVer = pickRandom(ANDROID_VERSIONS)
        val sdk = SDK_VERSIONS[androidVer]!!
        val (model, manufacturer) = pickRandom(MODELS)
        val brand = manufacturer
        val device = model.lowercase().replace(" ", "_")
        val product = device
        val hardware = pickRandom(HARDWARE_OPTIONS)
        val buildId = randomAlphanumeric(8).uppercase()
        val fingerprint = "$brand/$product/$device:$androidVer/$buildId/${randomHex(6)}:user/release-keys"
        
        return DeviceIdentity(
            // === Device Identifiers ===
            deviceId = generateDeviceId(),
            androidId = randomHex(16),
            gsfId = randomHex(16),
            advertisingId = UUID.randomUUID().toString(),
            
            // === Network ===
            wifiMac = generateMac(),
            bluetoothMac = generateMac(),
            wifiSsid = "${pickRandom(SSID_PREFIXES)}-${randomInt(1000, 99999)}",
            wifiBssid = generateMac(),
            
            // === Telephony ===
            imei = generateIMEI(),
            imsi = generateIMSI(pickRandom(COUNTRIES)),
            simSerial = "89${randomDigits(17)}",
            subscriberId = generateIMSI(pickRandom(COUNTRIES)),
            phoneNumber = generatePhone(),
            
            // === Build ===
            serialNumber = randomHex(12).lowercase(),
            brand = brand,
            model = model,
            manufacturer = manufacturer,
            device = device,
            product = product,
            hardware = hardware,
            fingerprint = fingerprint,
            hostname = "android-${randomHex(8).lowercase()}",
            display = "$model.$buildId",
            bootloader = pickRandom(BOOTLOADER_OPTIONS),
            
            // === Environment ===
            timezone = pickRandom(TIMEZONES),
            locale = pickRandom(LOCALES),
            country = pickRandom(COUNTRIES),
            kernelVersion = pickRandom(KERNEL_VERSIONS),
            osVersion = androidVer,
            sdkVersion = sdk,
            
            // === Hardware ===
            screenResolution = pickRandom(RESOLUTIONS),
            screenDensity = pickRandom(DENSITIES),
            totalRam = pickRandom(RAM_OPTIONS) * 1024 * 1024 * 1024,
            totalStorage = pickRandom(STORAGE_OPTIONS) * 1024 * 1024 * 1024,
            coreCount = pickRandom(CORE_OPTIONS),
            batteryLevel = random.nextFloat() * 0.7f + 0.2f,
            
            // === Google Accounts ===
            availableAccounts = emptyList()  // ** KHALI = koi Google account nahi **
        )
    }

    // ========== HELPERS ==========
    
    private fun generateDeviceId(): String = "35" + randomDigits(13)
    
    private fun generateIMEI(): String {
        val body = randomDigits(14)
        return body + luhnCheck(body)
    }
    
    private fun luhnCheck(digits: String): String {
        var sum = 0
        var alt = true
        for (i in digits.length - 1 downTo 0) {
            var n = digits[i].digitToInt()
            if (alt) { n *= 2; if (n > 9) n -= 9 }
            sum += n
            alt = !alt
        }
        return ((10 - (sum % 10)) % 10).toString()
    }
    
    private fun generateIMSI(country: String): String {
        val mcc = when (country) {
            "US" -> "310"; "IN" -> "404"; "GB" -> "234"
            "JP" -> "440"; "KR" -> "450"; "DE" -> "262"
            "FR" -> "208"; "BR" -> "724"; "AE" -> "424"
            "AU" -> "505"; else -> "310"
        }
        val mnc = pickRandom(listOf("01", "02", "10", "20", "30", "40"))
        val msin = randomDigits(10)
        return mcc + mnc + msin
    }
    
    private fun generatePhone(): String {
        val prefix = pickRandom(listOf("+1", "+91", "+44", "+82", "+86", "+49", "+33"))
        return prefix + randomDigits(10)
    }
    
    private fun generateMac(): String {
        val bytes = ByteArray(6)
        random.nextBytes(bytes)
        bytes[0] = (bytes[0].toInt() and 0xFE or 0x02).toByte()
        return bytes.joinToString(":") { "%02X".format(it) }
    }
    
    private fun randomHex(len: Int) = (1..len).map { "0123456789ABCDEF"[random.nextInt(16)] }.joinToString("")
    private fun randomDigits(len: Int) = (1..len).map { "0123456789"[random.nextInt(10)] }.joinToString("")
    private fun randomAlphanumeric(len: Int) = (1..len).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"[random.nextInt(36)] }.joinToString("")
    private fun randomInt(min: Int, max: Int) = random.nextInt(max - min) + min
    private fun <T> pickRandom(list: List<T>) = list[random.nextInt(list.size)]
}
