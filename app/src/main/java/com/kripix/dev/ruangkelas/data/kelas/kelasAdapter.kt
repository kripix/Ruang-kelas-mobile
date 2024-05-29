package com.kripix.dev.ruangkelas.data.kelas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kripix.dev.ruangkelas.databinding.ItemCardKelasBinding


class kelasAdapter (
    private val data: List<Kelas>,
    private val clickListener: kelasClickListener
)
    : RecyclerView.Adapter<kelasAdapter.CardViewHolder>() {

    class CardViewHolder(
        private val binding: ItemCardKelasBinding,
        private val clickListener: kelasClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Kelas) {
            binding.kelasImg.setImageResource(data.img)
            binding.kelasGrade.text = data.grade
            binding.kelasNama.text = data.kelas
            binding.kelasCreator.text = data.creator

            binding.kelasCard.setOnClickListener {
                clickListener.onClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemCardKelasBinding.inflate(from, parent, false)
        return CardViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(data[position])
    }
}