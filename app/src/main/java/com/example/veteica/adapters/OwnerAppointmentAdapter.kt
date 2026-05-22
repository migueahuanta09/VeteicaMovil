package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.OwnerAppointment

class OwnerAppointmentAdapter(
    private var appointments: List<OwnerAppointment>,
    private val onItemClick: (OwnerAppointment) -> Unit
) : RecyclerView.Adapter<OwnerAppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_owner_appointment, parent, false)
        return AppointmentViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun updateList(newList: List<OwnerAppointment>) {
        appointments = newList
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(
        itemView: android.view.View,
        private val onItemClick: (OwnerAppointment) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvConsulta: TextView = itemView.findViewById(R.id.tvConsulta)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvDiagnostico: TextView = itemView.findViewById(R.id.tvDiagnostico)
        private val tvVeterinario: TextView = itemView.findViewById(R.id.tvVeterinario)
        private var currentAppointment: OwnerAppointment? = null

        init {
            itemView.setOnClickListener {
                currentAppointment?.let { onItemClick(it) }
            }
        }

        fun bind(appointment: OwnerAppointment) {
            currentAppointment = appointment
            tvConsulta.text = appointment.consulta
            tvFecha.text = appointment.fecha
            tvDiagnostico.text = appointment.diagnostico
            tvVeterinario.text = appointment.veterinario
        }
    }
}