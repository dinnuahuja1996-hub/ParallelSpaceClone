package com.yourcompany.parallelspace.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.yourcompany.parallelspace.R
import com.yourcompany.parallelspace.model.AppClone

class CloneInfoDialog(context: Context, private val clone: AppClone) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_clone_info)
        findViewById<TextView>(R.id.tvAppName).text = clone.appName
        findViewById<TextView>(R.id.tvPackageName).text = clone.packageName
    }
}
