package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.PendingPayment
import java.text.NumberFormat
import java.util.Locale

class PendingPaymentAdapter(
    private var payments: List<PendingPayment>,
    private val onItemClick: (PendingPayment) -> Unit
) : RecyclerView.Adapter<PendingPaymentAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_payment, parent, false)
        return PaymentViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(payments[position])
    }

    override fun getItemCount(): Int = payments.size

    fun updateList(newList: List<PendingPayment>) {
        payments = newList
        notifyDataSetChanged()
    }

    class PaymentViewHolder(
        itemView: android.view.View,
        private val onItemClick: (PendingPayment) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvServiceName: TextView = itemView.findViewById(R.id.tvServiceName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private var currentPayment: PendingPayment? = null

        init {
            itemView.setOnClickListener {
                currentPayment?.let { onItemClick(it) }
            }
        }

        fun bind(payment: PendingPayment) {
            currentPayment = payment
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

            tvId.text = payment.id.toString()
            tvPetName.text = payment.petName
            tvServiceName.text = payment.serviceName
            tvDate.text = payment.date
            tvTotal.text = format.format(payment.total)
            tvStatus.text = payment.status

            if (payment.status == "Cobrado") {
                tvStatus.setTextColor(itemView.context.getColor(R.color.veteica_green))
            } else {
                tvStatus.setTextColor(itemView.context.getColor(R.color.veteica_orange))
            }
        }
    }
}