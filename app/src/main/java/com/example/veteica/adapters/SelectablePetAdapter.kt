package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Pet

class SelectablePetAdapter(
    private var pets: List<Pet>,
    private val onItemClick: (Pet) -> Unit
) : RecyclerView.Adapter<SelectablePetAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selectable_pet, parent, false)
        return PetViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount(): Int = pets.size

    class PetViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Pet) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetSpecies: TextView = itemView.findViewById(R.id.tvPetSpecies)
        private val tvPetOwner: TextView = itemView.findViewById(R.id.tvPetOwner)
        private var currentPet: Pet? = null

        init {
            itemView.setOnClickListener {
                currentPet?.let { onItemClick(it) }
            }
        }

        fun bind(pet: Pet) {
            currentPet = pet
            tvPetName.text = pet.name
            tvPetSpecies.text = pet.species
            tvPetOwner.text = "Dueño: ${pet.ownerName}"
        }
    }
}