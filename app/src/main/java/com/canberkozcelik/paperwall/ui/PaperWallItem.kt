package com.canberkozcelik.paperwall.ui

import android.net.Uri
import android.view.View
import com.canberkozcelik.paperwall.R
import com.canberkozcelik.paperwall.databinding.ListItemPaperBinding
import com.canberkozcelik.paperwall.extension.setSafeOnClickListener
import com.xwray.groupie.viewbinding.BindableItem

class PaperWallItem(private val imageUri: Uri, private val viewModel: PaperWallViewModel) :
    BindableItem<ListItemPaperBinding>() {

    override fun getLayout() = R.layout.list_item_paper

    override fun bind(viewBinding: ListItemPaperBinding, position: Int) {
        viewBinding.imageUri = imageUri
        viewBinding.root.setSafeOnClickListener {
            viewModel.onImageClicked(imageUri)
        }
    }

    override fun initializeViewBinding(view: View): ListItemPaperBinding {
        return ListItemPaperBinding.bind(view)
    }
}
