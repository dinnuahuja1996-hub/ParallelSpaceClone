package com.yourcompany.parallelspace.app

import android.app.Application

class ParallelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application initialized - CloneManager instances are created per-Activity
    }
}
