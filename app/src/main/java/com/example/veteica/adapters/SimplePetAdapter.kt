package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.SimplePet

class SimplePetAdapter(
    private var pets: List<SimplePet>,
    private val onRemoveClick: (SimplePet, Int) -> Unit
) : RecyclerView.Adapter<SimplePetAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_create_pet_simple, parent, false)
        return PetViewHolder(view, onRemoveClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position], position)
    }

    override fun getItemCount(): Int = pets.size

    fun updateList(newList: List<SimplePet>) {
        pets = newList
        notifyDataSetChanged()
    }

    class PetViewHolder(
        itemView: android.view.View,
        private val onRemoveClick: (SimplePet, Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemovePet)
        private var currentPet: SimplePet? = null
        private var currentPosition: Int = -1

        init {
            btnRemove.setOnClickListener {
                currentPet?.let { currentPosition?.let { pos ->
                    onRemoveClick(it, pos)
                } }
            }
        }

        fun bind(pet: SimplePet, position: Int) {
            currentPet = pet
            currentPosition = position
            tvPetName.text = pet.name
        }
    }
}