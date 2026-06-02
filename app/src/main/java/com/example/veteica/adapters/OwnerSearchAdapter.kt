package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Owner

class OwnerSearchAdapter(
    private var owners: List<Owner>,
    private val onItemClick: (Owner) -> Unit
) : RecyclerView.Adapter<OwnerSearchAdapter.OwnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_owner_search, parent, false)
        return OwnerViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        holder.bind(owners[position])
    }

    override fun getItemCount(): Int = owners.size

    class OwnerViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Owner) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private var currentOwner: Owner? = null

        init {
            itemView.setOnClickListener {
                currentOwner?.let { onItemClick(it) }
            }
        }

        fun bind(owner: Owner) {
            currentOwner = owner
            tvName.text = owner.name
            tvCode.text = "Código: ${owner.uniqueCode}"
            tvPhone.text = owner.phone
        }
    }
}