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
import com.squareup.picasso.Picasso

class IconKelasAdapter(
    private val icons: ArrayList<IconKelasModel.Data>
) : RecyclerView.Adapter<IconKelasAdapter.IconKelasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconKelasViewHolder {
        val binding = ItemIconKelasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconKelasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconKelasViewHolder, position: Int) {
        Log.d("IconKelasAdapter", "onBindViewHolder called for position: $position")
        holder.bind(icons[position])
    }

    override fun getItemCount(): Int = icons.size

    class IconKelasViewHolder(
        private val binding: ItemIconKelasBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(icon: IconKelasModel.Data) {
            Picasso.get().load(icon.icon_kelas).into(binding.iconKelasImg)
        }
    }

    fun setData(data: List<IconKelasModel.Data>) {
        icons.clear()
        icons.addAll(data)
        notifyDataSetChanged()
    }
}

