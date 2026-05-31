package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Pet

class OwnerEditPetAdapter(
    private var pets: List<Pet>,
    private val onRemoveClick: (Pet, Int) -> Unit
) : RecyclerView.Adapter<OwnerEditPetAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_owner_edit_pet, parent, false)
        return PetViewHolder(view, onRemoveClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position], position)
    }

    override fun getItemCount(): Int = pets.size

    fun updateList(newList: List<Pet>) {
        pets = newList
        notifyDataSetChanged()
    }

    class PetViewHolder(
        itemView: android.view.View,
        private val onRemoveClick: (Pet, Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetSpecies: TextView = itemView.findViewById(R.id.tvPetSpecies)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemovePet)
        private var currentPet: Pet? = null
        private var currentPosition: Int = -1

        init {
            btnRemove.setOnClickListener {
                currentPet?.let { currentPosition?.let { pos ->
                    onRemoveClick(it, pos)
                } }
            }
        }

        fun bind(pet: Pet, position: Int) {
            currentPet = pet
            currentPosition = position
            tvPetName.text = pet.name
            tvPetSpecies.text = "${pet.species} - ${pet.breed}"
        }
    }
}