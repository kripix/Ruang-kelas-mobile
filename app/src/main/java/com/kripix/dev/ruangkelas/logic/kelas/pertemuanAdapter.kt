package com.kripix.dev.ruangkelas.logic.kelas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kripix.dev.ruangkelas.databinding.ItemPertemuanBinding


class pertemuanAdapter (
    private val data: List<Pertemuan>,
    private val clickListener: pertemuanClickListener
)
    : RecyclerView.Adapter<pertemuanAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemPertemuanBinding,
        private val clickListener: pertemuanClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pertemuan) {
            binding.textNumber.text = data.nomor.toString()
            binding.circleContainer.setOnClickListener {
                clickListener.onClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemPertemuanBinding.inflate(from, parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }
}