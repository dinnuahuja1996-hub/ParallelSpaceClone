// ui/adapters/CloneGridAdapter.kt
package com.yourcompany.parallelspace.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.parallelspace.R
import com.yourcompany.parallelspace.model.AppClone

class CloneGridAdapter(
    private var clones: MutableList<AppClone>,
    private val onClick: (AppClone) -> Unit
) : RecyclerView.Adapter<CloneGridAdapter.ViewHolder>() {

    private var onLongClick: ((AppClone) -> Unit)? = null

    fun setOnLongClickListener(listener: (AppClone) -> Unit) {
        onLongClick = listener
    }

    fun updateList(newClones: List<AppClone>) {
        clones.clear()
        clones.addAll(newClones)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clone_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clone = clones[position]
        
        holder.icon.setImageDrawable(clone.icon)
        holder.name.text = clone.appName
        
        // Per-clone identity badges
        val identity = clone.identity
        holder.tvDeviceId.text = "ID: ${identity.deviceId.take(8)}..."
        holder.tvMac.text = "MAC: ${identity.wifiMac}"
        holder.tvModel.text = "${identity.brand} ${identity.model}"
        
        // Permission badge — 21/21
        holder.tvPermissions.text = "${clone.grantedPermissions.size} permissions ✓"
        
        // Google service tag
        holder.tvGoogleTag.visibility = if (clone.isGoogleService) View.VISIBLE else View.GONE
        
        // Random badge indicator
        holder.itemView.setBackgroundResource(
            if (position % 2 == 0) R.drawable.card_bg_light 
            else R.drawable.card_bg_dark
        )

        holder.itemView.setOnClickListener { onClick(clone) }
        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(clone)
            true
        }
    }

    override fun getItemCount() = clones.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        val name: TextView = itemView.findViewById(R.id.tvAppName)
        val tvDeviceId: TextView = itemView.findViewById(R.id.tvDeviceId)
        val tvMac: TextView = itemView.findViewById(R.id.tvMac)
        val tvModel: TextView = itemView.findViewById(R.id.tvModel)
        val tvPermissions: TextView = itemView.findViewById(R.id.tvPermissions)
        val tvGoogleTag: TextView = itemView.findViewById(R.id.tvGoogleTag)
    }
}
