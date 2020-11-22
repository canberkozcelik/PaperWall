package com.canberkozcelik.paperwall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.canberkozcelik.paperwall.R
import com.canberkozcelik.paperwall.common.NavigationEvent
import com.canberkozcelik.paperwall.databinding.FragmentPaperWallBinding
import com.canberkozcelik.paperwall.extension.setSafeOnClickListener
import com.canberkozcelik.paperwall.extension.showToast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.flow.collect
import pub.devrel.easypermissions.EasyPermissions

class PaperWallFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    lateinit var binding: FragmentPaperWallBinding

    @VisibleForTesting
    val paperWallViewModel: PaperWallViewModel by viewModels()

    companion object PaperListConstants {
        const val REQUEST_CODE_READ_EXTERNAL_STORAGE: Int = 1123
        const val REQUEST_CODE_SELECT_IMAGES: Int = 5813
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_paper_wall, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        binding.root.setSafeOnClickListener { checkReadExternalStoragePermission() }
        binding.rvSelectedPapers.adapter = groupAdapter

        lifecycleScope.launchWhenCreated {
            paperWallViewModel.container.stateFlow.collect {
                reduce(groupAdapter, it)
            }
        }

        lifecycleScope.launchWhenCreated {
            paperWallViewModel.container.sideEffectFlow.collect {
                sideEffect(it)
            }
        }
    }

    private fun sideEffect(event: NavigationEvent) {
        when (event) {
            is OpenPaperNavigationEvent ->
                requireContext().showToast("Clicked image with URI: ${event.uri}")
        }
    }

    private fun reduce(adapter: GroupAdapter<GroupieViewHolder>, state: PaperWallState) {
        adapter.update(state.selectedImageUris.map { PaperWallItem(it, paperWallViewModel) })
    }

    private fun checkReadExternalStoragePermission() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            resources.getString(R.string.read_extarnal_permission_rationale),
            REQUEST_CODE_READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGES) {
            val selectedImageUris = arrayListOf<Uri>()
            data?.let { intent ->
                intent.clipData?.let { clipData ->
                    // User picked multiple images
                    for (i in 0 until clipData.itemCount) {
                        selectedImageUris.add(clipData.getItemAt(i).uri)
                    }
                }
                intent.data?.let { uri ->
                    selectedImageUris.add(uri)
                }
            }
            if (selectedImageUris.isEmpty()) {
                requireContext().showToast(R.string.no_image_selected)
                return
            }
            paperWallViewModel.container.currentState.copy(selectedImageUris = selectedImageUris)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            requireContext().showToast(R.string.read_extarnal_permission_denied)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            openPhotos()
        }
    }

    private fun openPhotos() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.chooser_select_picture)
            ), REQUEST_CODE_SELECT_IMAGES
        )
    }
}