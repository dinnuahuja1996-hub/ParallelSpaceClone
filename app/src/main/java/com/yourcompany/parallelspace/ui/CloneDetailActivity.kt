// ui/CloneDetailActivity.kt
package com.yourcompany.parallelspace.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourcompany.parallelspace.core.CloneManager
import com.yourcompany.parallelspace.core.PermissionManager
import com.yourcompany.parallelspace.databinding.ActivityCloneDetailBinding
import com.yourcompany.parallelspace.model.AppClone
import com.yourcompany.parallelspace.ui.adapters.PermissionAdapter

class CloneDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloneDetailBinding
    private lateinit var clone: AppClone
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloneDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cloneId = intent.getStringExtra("clone_id") ?: return
        val packageName = intent.getStringExtra("package_name") ?: return
        val cloneName = intent.getStringExtra("clone_name") ?: "Clone"

        permissionManager = PermissionManager(this)
        
        // Identity info show kare
        val identity = com.yourcompany.parallelspace.identity.IdentityGenerator().generate()
        
        binding.tvTitle.text = cloneName
        binding.tvPackage.text = packageName
        
        // Identity section
        binding.tvIdentityInfo.text = """
            📱 Device: ${identity.brand} ${identity.model}
            🆔 Device ID: ${identity.deviceId}
            📱 IMEI: ${identity.imei}
            📶 WiFi MAC: ${identity.wifiMac}
            🔵 BT MAC: ${identity.bluetoothMac}
            🆔 Android ID: ${identity.androidId}
            🆔 GAID: ${identity.advertisingId}
            📍 Locale: ${identity.locale}
            🕐 Timezone: ${identity.timezone}
            🌍 Country: ${identity.country}
        """.trimIndent()
        
        // Permissions — SAB GRANTED
        val permissions = permissionManager.getAllPermissions(packageName)
        val grantedCount = permissions.count { it.isGranted }
        val totalCount = permissions.size
        
        binding.tvPermissionSummary.text = "✅ $grantedCount / $totalCount permissions granted"
        binding.progressBar.max = totalCount
        binding.progressBar.progress = grantedCount
        
        // Group by category
        val grouped = permissions.groupBy { it.group }
        binding.tvLocationCount.text = "${grouped["Location"]?.size ?: 0} granted"
        binding.tvNetworkCount.text = "${grouped["Network"]?.size ?: 0} granted"
        binding.tvGoogleCount.text = "${grouped["Google Services"]?.size?.plus(grouped["Google Play"]?.size ?: 0)?.plus(grouped["Advertising"]?.size ?: 0)} granted"
        binding.tvDeviceCount.text = "${grouped["Device"]?.size ?: 0} granted"
        binding.tvBiometricCount.text = "${grouped["Biometric"]?.size ?: 0} granted"
        binding.tvSystemCount.text = "${grouped["System"]?.size ?: 0} granted"
        
        // Full permission list
        val adapter = PermissionAdapter(permissions)
        binding.recyclerPermissions.layoutManager = LinearLayoutManager(this)
        binding.recyclerPermissions.adapter = adapter
        
        // Regenerate Identity button
        binding.btnRegenerate.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Generate New Random Identity?")
                .setMessage("This will create a completely new device fingerprint for this clone: new Device ID, IMEI, MAC, Android ID, advertising ID, model, everything.")
                .setPositiveButton("Generate New") { _, _ ->
                    Toast.makeText(this, 
                        "🆕 New random identity generated!\n" +
                        "Device ID: ${identity.deviceId}\n" +
                        "MAC: ${identity.wifiMac}\n" +
                        "IMEI: ${identity.imei}", 
                        Toast.LENGTH_LONG).show()
                    recreate()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
