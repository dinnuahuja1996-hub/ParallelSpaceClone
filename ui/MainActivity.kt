// ui/MainActivity.kt
package com.yourcompany.parallelspace.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yourcompany.parallelspace.R
import com.yourcompany.parallelspace.core.CloneManager
import com.yourcompany.parallelspace.databinding.ActivityMainBinding
import com.yourcompany.parallelspace.model.AppClone
import com.yourcompany.parallelspace.ui.adapters.CloneGridAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cloneManager: CloneManager
    private lateinit var adapter: CloneGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cloneManager = CloneManager(this)

        setupRecyclerView()
        setupFab()
        refreshClones()
    }

    private fun setupRecyclerView() {
        adapter = CloneGridAdapter(mutableListOf()) { clone ->
            // Click on clone → launch with full spoofing
            cloneManager.launchClone(clone)
            Toast.makeText(this, 
                "Launching ${clone.appName}\n🔐 Device: ${clone.identity.model}\n📱 ID: ${clone.identity.deviceId.take(10)}...\n📶 MAC: ${clone.identity.wifiMac}\n🆔 ADID: ${clone.identity.advertisingId.take(10)}...", 
                Toast.LENGTH_LONG).show()
        }
        
        binding.recyclerClones.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = this@MainActivity.adapter
        }
        
        adapter.setOnLongClickListener { clone ->
            showCloneOptions(clone)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAppSelectionDialog()
        }
    }

    private fun refreshClones() {
        val clones = cloneManager.getAllClones()
        adapter.updateList(clones)
        
        binding.emptyState.visibility = if (clones.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.recyclerClones.visibility = if (clones.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        
        // Update subtitle
        val grantedCount = CloneManager.GRANTED_PERMISSIONS.size
        binding.tvSubtitle.text = "${clones.size} clones • $grantedCount permissions granted"
    }

    private fun showAppSelectionDialog() {
        // Get installed apps
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val activities = pm.queryIntentActivities(intent, 0)
        
        val apps = activities
            .map { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                val name = resolveInfo.loadLabel(pm).toString()
                val icon = resolveInfo.loadIcon(pm)
                Triple(pkg, name, icon)
            }
            .distinctBy { it.first }
            .sortedBy { it.second }
        
        val appNames = apps.map { it.second }.toTypedArray()
        val icons = apps.map { it.third }
        
        val dialog = AlertDialog.Builder(this)
            .setTitle("Select App to Clone")
            .setItems(appNames) { _, which ->
                val (packageName, appName, icon) = apps[which]
                createNewClone(packageName, appName, icon)
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }

    private fun createNewClone(packageName: String, appName: String, icon: android.graphics.drawable.Drawable?) {
        // Har baar new clone with FRESH random identity
        val clone = cloneManager.createClone(packageName)
        
        // Show identity summary
        showIdentitySummary(clone)
        
        refreshClones()
    }

    private fun showIdentitySummary(clone: AppClone) {
        val identity = clone.identity
        
        AlertDialog.Builder(this)
            .setTitle("✅ ${clone.appName} Created")
            .setMessage("""
                ── NEW RANDOM IDENTITY ──
                
                📱 Model: ${identity.brand} ${identity.model}
                🆔 Device ID: ${identity.deviceId}
                📱 IMEI: ${identity.imei}
                📶 WiFi MAC: ${identity.wifiMac}
                🔵 BT MAC: ${identity.bluetoothMac}
                🆔 Android ID: ${identity.androidId}
                🆔 AD ID (GAID): ${identity.advertisingId}
                📍 Locale: ${identity.locale}
                🕐 Timezone: ${identity.timezone}
                🌍 Country: ${identity.country}
                
                ✅ 21 Permissions: ALL GRANTED
                🔐 Google Accounts: HIDDEN
            """.trimIndent())
            .setPositiveButton("Launch", null) { _, _ ->
                cloneManager.launchClone(clone)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCloneOptions(clone: AppClone) {
        val options = arrayOf("Launch", "View Permissions", "View Identity", "Delete Clone")
        
        AlertDialog.Builder(this)
            .setTitle(clone.appName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> cloneManager.launchClone(clone)
                    1 -> openPermissions(clone)
                    2 -> showIdentitySummary(clone)
                    3 -> deleteClone(clone)
                }
            }
            .show()
    }

    private fun openPermissions(clone: AppClone) {
        val intent = Intent(this, CloneDetailActivity::class.java)
        intent.putExtra("clone_id", clone.id)
        intent.putExtra("clone_name", clone.appName)
        intent.putExtra("package_name", clone.packageName)
        startActivity(intent)
    }

    private fun deleteClone(clone: AppClone) {
        AlertDialog.Builder(this)
            .setTitle("Delete ${clone.appName}?")
            .setMessage("This will permanently remove this clone and its identity.")
            .setPositiveButton("Delete") { _, _ ->
                cloneManager.deleteClone(clone.id)
                refreshClones()
                Toast.makeText(this, "Clone deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshClones()
    }
}
