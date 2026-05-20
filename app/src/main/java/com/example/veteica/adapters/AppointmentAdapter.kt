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
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
        holder.itemView.setOnClickListener { onItemClick(appointment) }
    }

    override fun getItemCount(): Int = appointments.size

    fun updateList(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvReason: TextView = itemView.findViewById(R.id.tvReason)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(appointment: Appointment) {
            tvPetName.text = appointment.petName
            tvDateTime.text = "${appointment.date} - ${appointment.time}"
            tvReason.text = appointment.reason
            tvStatus.text = appointment.status
            tvStatus.setTextColor(
                if (appointment.status == "Confirmada")
                    itemView.context.getColor(R.color.veteica_green)
                else
                    itemView.context.getColor(R.color.veteica_orange)
            )
        }
    }
}