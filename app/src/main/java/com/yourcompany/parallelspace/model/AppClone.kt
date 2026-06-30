// model/AppClone.kt
package com.yourcompany.parallelspace.model

import android.graphics.drawable.Drawable

data class AppClone(
    val id: String,                    // Unique clone ID (UUID)
    val packageName: String,           // Original app package
    val appName: String,               // Display name
    val icon: Drawable?,               // App icon
    val identity: DeviceIdentity,      // ** COMPLETELY RANDOM per clone **
    val grantedPermissions: List<String>,  // 21 granted permissions
    val pendingPermissions: List<String>,  // Empty (sab granted)
    val isGoogleService: Boolean,      // Google app ya nahi
    val createdAt: Long,
    val lastUsed: Long
)
