package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Service
import java.text.NumberFormat
import java.util.Locale

class ServiceAdapter(
    private var services: List<Service>,
    private val onItemClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount(): Int = services.size

    fun updateList(newList: List<Service>) {
        services = newList
        notifyDataSetChanged()
    }

    class ServiceViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Service) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvVeterinarians: TextView = itemView.findViewById(R.id.tvVeterinarians)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private var currentService: Service? = null

        init {
            itemView.setOnClickListener {
                currentService?.let { onItemClick(it) }
            }
        }

        fun bind(service: Service) {
            currentService = service
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

            tvQuantity.text = service.quantity.toString()
            tvName.text = service.name
            tvVeterinarians.text = service.vets.toString()
            tvPrice.text = format.format(service.price)
        }
    }
}