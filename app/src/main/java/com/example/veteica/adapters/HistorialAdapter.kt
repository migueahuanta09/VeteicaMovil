package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Historial

class HistorialAdapter(
    private var historialList: List<Historial>,
    private val onItemClick: (Historial) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        holder.bind(historialList[position])
    }

    override fun getItemCount(): Int = historialList.size

    class HistorialViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Historial) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvConsulta: TextView = itemView.findViewById(R.id.tvConsulta)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvDiagnostico: TextView = itemView.findViewById(R.id.tvDiagnostico)
        private val tvVeterinario: TextView = itemView.findViewById(R.id.tvVeterinario)
        private var currentHistorial: Historial? = null

        init {
            itemView.setOnClickListener {
                currentHistorial?.let { onItemClick(it) }
            }
        }

        fun bind(historial: Historial) {
            currentHistorial = historial
            tvConsulta.text = historial.consulta
            tvFecha.text = historial.fecha
            tvDiagnostico.text = historial.diagnostico
            tvVeterinario.text = historial.veterinario
        }
    }
}