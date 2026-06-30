package com.yourcompany.parallelspace.app

import android.app.Application
import com.yourcompany.parallelspace.core.CloneManager

class ParallelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CloneManager.init(this)
    }
}
