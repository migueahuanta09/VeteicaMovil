package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Appointment

class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_row, parent, false)
        return AppointmentViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun updateList(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Appointment) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvOwnerName: TextView = itemView.findViewById(R.id.tvOwnerName)
        private val tvVeterinarian: TextView = itemView.findViewById(R.id.tvVeterinarian)
        private var currentAppointment: Appointment? = null

        init {
            itemView.setOnClickListener {
                currentAppointment?.let { onItemClick(it) }
            }
        }

        fun bind(appointment: Appointment) {
            currentAppointment = appointment
            tvDate.text = appointment.date
            tvTime.text = appointment.time
            tvPetName.text = appointment.petName
            tvOwnerName.text = appointment.ownerName
            tvVeterinarian.text = appointment.veterinarian
        }
    }
}