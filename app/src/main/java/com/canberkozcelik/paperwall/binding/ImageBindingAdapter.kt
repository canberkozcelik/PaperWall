package com.canberkozcelik.paperwall.binding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ImageBindingAdapter {

    @JvmStatic
    @BindingAdapter("setImageForUri")
    fun setImageForUri(view: ImageView, uri: Uri?) {
        uri?.let {
            Glide.with(view.context)
                .load(it)
                .into(view)
        }
    }
}