package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Vacuna

class VacunaAdapter(
    private var vacunasList: List<Vacuna>,
    private val onItemClick: (Vacuna) -> Unit
) : RecyclerView.Adapter<VacunaAdapter.VacunaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacunaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vacuna, parent, false)
        return VacunaViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: VacunaViewHolder, position: Int) {
        holder.bind(vacunasList[position])
    }

    override fun getItemCount(): Int = vacunasList.size

    class VacunaViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Vacuna) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvVacunaNombre: TextView = itemView.findViewById(R.id.tvVacunaNombre)
        private val tvVacunaCantidad: TextView = itemView.findViewById(R.id.tvVacunaCantidad)
        private val tvVacunaFecha: TextView = itemView.findViewById(R.id.tvVacunaFecha)
        private var currentVacuna: Vacuna? = null

        init {
            itemView.setOnClickListener {
                currentVacuna?.let { onItemClick(it) }
            }
        }

        fun bind(vacuna: Vacuna) {
            currentVacuna = vacuna
            tvVacunaNombre.text = vacuna.nombre
            tvVacunaCantidad.text = vacuna.cantidad
            tvVacunaFecha.text = vacuna.fecha
        }
    }
}