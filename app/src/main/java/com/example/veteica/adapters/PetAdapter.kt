package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Pet

class PetAdapter(
    private var pets: List<Pet>,
    private val onItemClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = pets[position]
        holder.bind(pet)
    }

    override fun getItemCount(): Int = pets.size

    fun updateList(newList: List<Pet>) {
        pets = newList
        notifyDataSetChanged()
    }

    class PetViewHolder(
        itemView: View,
        private val onItemClick: (Pet) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvEspecie: TextView = itemView.findViewById(R.id.tvEspecie)
        private val tvDueno: TextView = itemView.findViewById(R.id.tvDueno)
        private var currentPet: Pet? = null

        init {
            itemView.setOnClickListener {
                currentPet?.let { onItemClick(it) }
            }
        }

        fun bind(pet: Pet) {
            currentPet = pet
            tvId.text = pet.id.toString()
            tvNombre.text = pet.name
            tvEspecie.text = pet.species
            tvDueno.text = pet.ownerName
        }
    }
}