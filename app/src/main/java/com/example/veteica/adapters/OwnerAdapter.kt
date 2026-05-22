package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Owner

class OwnerAdapter(
    private var owners: List<Owner>,
    private val onItemClick: (Owner) -> Unit
) : RecyclerView.Adapter<OwnerAdapter.OwnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_owner, parent, false)
        return OwnerViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        holder.bind(owners[position])
    }

    override fun getItemCount(): Int = owners.size

    fun updateList(newList: List<Owner>) {
        owners = newList
        notifyDataSetChanged()
    }

    class OwnerViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Owner) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvPetsCount: TextView = itemView.findViewById(R.id.tvPetsCount)
        private var currentOwner: Owner? = null

        init {
            itemView.setOnClickListener {
                currentOwner?.let { onItemClick(it) }
            }
        }

        fun bind(owner: Owner) {
            currentOwner = owner
            tvId.text = owner.id.toString()
            tvName.text = owner.name
            tvPhone.text = owner.phone
            tvPetsCount.text = "${owner.petsCount} mascotas"
        }
    }
}