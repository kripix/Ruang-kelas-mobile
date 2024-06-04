package com.kripix.dev.ruangkelas.ui.kelas


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kripix.dev.ruangkelas.databinding.ItemCardKelasBinding
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.squareup.picasso.Picasso

class KelasAdapter (
    val kelas: ArrayList<KelasModel.Data>,
    val clickListener: KelasClickListener
): RecyclerView.Adapter<KelasAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemCardKelasBinding.inflate(from, parent, false)
        return ViewHolder(binding, clickListener)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(kelas[position])
    }

    override fun getItemCount() = kelas.size

    class ViewHolder(
        val binding: ItemCardKelasBinding,
        val clickListener: KelasClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: KelasModel.Data) {
            Picasso.get().load(data.icon_kelas).into(binding.kelasImg)
            binding.kelasGrade.text = data.nama_grade
            binding.kelasNama.text = data.nama_kelas
            binding.kelasCreator.text = data.wali_kelas

            binding.kelasCard.setOnClickListener {
                clickListener.onClick(data)
            }


        }
    }

    public fun setData(data: List<KelasModel.Data>){
        kelas.clear()
        kelas.addAll(data)
        notifyDataSetChanged()
    }
}