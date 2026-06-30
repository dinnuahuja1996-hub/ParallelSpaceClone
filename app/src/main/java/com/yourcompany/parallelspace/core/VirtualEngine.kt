package com.yourcompany.parallelspace.core

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.yourcompany.parallelspace.model.AppClone

object VirtualEngine {
    fun getInstalledApps(context: Context): List<ApplicationInfo> {
        val pm = context.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .filter { it.packageName != context.packageName }
    }

    fun startClone(context: Context, clone: AppClone): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(clone.packageName)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
