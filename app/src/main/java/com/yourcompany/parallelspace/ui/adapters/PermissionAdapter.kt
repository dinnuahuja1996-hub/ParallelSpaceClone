package com.yourcompany.parallelspace.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.parallelspace.R
import com.yourcompany.parallelspace.model.AppPermission

class PermissionAdapter(private val permissions: List<AppPermission>) :
    RecyclerView.Adapter<PermissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_permission, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val perm = permissions[position]
        holder.tvLabel.text = perm.label
        holder.tvName.text = perm.name
        holder.tvGroup.text = perm.group
        holder.tvStatus.text = if (perm.isGranted) "✅ GRANTED" else "❌ DENIED"
        holder.tvStatus.setTextColor(
            if (perm.isGranted) 0xFF2E7D32.toInt() else 0xFFC62828.toInt()
        )
        holder.tvLevel.text = perm.protectionLevel
    }

    override fun getItemCount() = permissions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLabel: TextView = itemView.findViewById(R.id.tvPermLabel)
        val tvName: TextView = itemView.findViewById(R.id.tvPermName)
        val tvGroup: TextView = itemView.findViewById(R.id.tvPermGroup)
        val tvStatus: TextView = itemView.findViewById(R.id.tvPermStatus)
        val tvLevel: TextView = itemView.findViewById(R.id.tvPermLevel)
    }
}
