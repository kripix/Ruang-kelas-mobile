package com.kripix.dev.ruangkelas.bg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.bumptech.glide.Glide
import com.kripix.dev.ruangkelas.KelasFragment
import com.kripix.dev.ruangkelas.MainActivity
import com.kripix.dev.ruangkelas.R


class bgAdapter (
    private val context: Context,
    private val itemList: List<MenuBackgroundItem>,
    private val onBackgroundClickListener: (MenuBackgroundItem) -> Unit
) : ArrayAdapter<MenuBackgroundItem>(context, 0, itemList) {
    override fun getCount(): Int = itemList.size
    override fun getItem(position: Int): MenuBackgroundItem = itemList[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bg, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val backgroundItem = itemList[position]
        holder.bind(backgroundItem)

        view?.setOnClickListener {
            onBackgroundClickListener(backgroundItem)
        }

        return view!!
    }

    private class ViewHolder(view: View) {
        private val backgroundImageView: ImageView = view.findViewById(R.id.img_item_bg)
        private val backgroundTextView: TextView = view.findViewById(R.id.tv_item_bg)

        fun bind(backgroundItem: MenuBackgroundItem) {
            backgroundImageView.setImageResource(backgroundItem.image)
            backgroundTextView.text = backgroundItem.name
        }
    }


}