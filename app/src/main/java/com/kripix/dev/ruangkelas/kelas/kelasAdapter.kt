package com.kripix.dev.ruangkelas.kelas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kripix.dev.ruangkelas.databinding.ItemCardKelasBinding


class kelasAdapter (private val data: List<kelas>)
    : RecyclerView.Adapter<kelasAdapter.CardViewHolder>() {

    class CardViewHolder(
        private val binding: ItemCardKelasBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: kelas) {
            binding.kelasImg.setImageResource(data.img)
            binding.kelasName.text = data.kelas
            binding.kelasTitle.text = data.mapel
            binding.kelasCreator.text = data.creator
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemCardKelasBinding.inflate(from, parent, false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(data[position])
    }
}