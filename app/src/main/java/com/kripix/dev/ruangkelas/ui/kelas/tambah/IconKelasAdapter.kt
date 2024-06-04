import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.data.model.IconKelasModel
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.databinding.ItemIconKelasBinding
import com.kripix.dev.ruangkelas.ui.kelas.tambah.IconClickListener
import com.squareup.picasso.Picasso

class IconKelasAdapter(
    private val icons: ArrayList<IconKelasModel.Data>,
    private val clickListener: IconClickListener
) : RecyclerView.Adapter<IconKelasAdapter.IconKelasViewHolder>() {

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconKelasViewHolder {
        val binding = ItemIconKelasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconKelasViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: IconKelasViewHolder, position: Int) {
        Log.e("IconKelasAdapter", "onBindViewHolder called for position: $position with data: ${icons[position]}")
        holder.bind(icons[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = icons.size

    inner class IconKelasViewHolder(
        private val binding: ItemIconKelasBinding,
        private val clickListener: IconClickListener

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(icon: IconKelasModel.Data, isSelected: Boolean) {
            Log.e("IconKelasViewHolder", "Binding icon: ${icon.icon_kelas}")
            Picasso.get().load(icon.icon_kelas).into(binding.iconKelasImg)
            binding.translucentLayer.visibility = if (isSelected) View.VISIBLE else View.GONE

            binding.iconKelasCard.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = adapterPosition
                    notifyItemChanged(selectedPosition)
                    clickListener.onClick(icon)
                }
            }
        }
    }

    fun setData(data: List<IconKelasModel.Data>) {
        icons.clear()
        icons.addAll(data)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        notifyItemChanged(selectedPosition)
        selectedPosition = -1
    }
}