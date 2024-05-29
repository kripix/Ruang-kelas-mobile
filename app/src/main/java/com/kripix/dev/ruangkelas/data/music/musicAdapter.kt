package com.kripix.dev.ruangkelas.data.music

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kripix.dev.ruangkelas.R
import com.kripix.dev.ruangkelas.databinding.BottomSheetOptionMusicBinding
import com.kripix.dev.ruangkelas.databinding.ItemMusicBinding


class musicAdapter(
    context: Context,
    private val musicList: List<MenuMusicItem>,
    private val onMusicClickListener: (MenuMusicItem) -> Unit
) : ArrayAdapter<MenuMusicItem>(context, 0, musicList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var binding: ItemMusicBinding

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_music, parent, false).also {
            binding = ItemMusicBinding.bind(it)
            it.tag = binding
        }

        binding = view.tag as ItemMusicBinding
        val musicItem = getItem(position)

        binding.apply {
            imgCoverList.setImageResource(musicItem?.image ?: 0)
            tvJudulList.text = musicItem?.judul ?: ""
            tvAuthorList.text = musicItem?.author ?: ""
        }

        view.setOnClickListener {
            musicItem?.let { onMusicClickListener(it) }
        }

        return view
    }
}




