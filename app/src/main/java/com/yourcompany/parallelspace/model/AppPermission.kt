// model/AppPermission.kt
package com.yourcompany.parallelspace.model

data class AppPermission(
    val name: String,          // Full permission name
    val label: String,         // User-friendly label
    val isGranted: Boolean,    // true = granted, false = pending
    val group: String,         // Location, Phone, Storage, etc.
    val protectionLevel: String // normal, dangerous, signature
)
