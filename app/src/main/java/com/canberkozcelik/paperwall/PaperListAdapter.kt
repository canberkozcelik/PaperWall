package com.canberkozcelik.paperwall

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

/**
 * Created by canberkozcelik on 24.03.2019.
 */
class PaperListAdapter(private val selectedImageUris: ArrayList<Uri>?) : RecyclerView.Adapter<PaperListAdapter.ViewHolder>() {

    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_paper, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return selectedImageUris!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.item.context).load(selectedImageUris?.get(position))
            .into(holder.item.findViewById(R.id.image_paper))
    }
}