package com.canberkozcelik.paperwall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.canberkozcelik.paperwall.databinding.FragmentPaperWallBinding
import pub.devrel.easypermissions.EasyPermissions


class PaperWallFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object PaperListConstants {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE: Int = 1123
        private const val REQUEST_CODE_SELECT_IMAGES: Int = 5813
    }

    private lateinit var binding: FragmentPaperWallBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_paper_wall, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.root.setOnClickListener { initPermission() }
    }

    private fun initPermission() {
        if (EasyPermissions.hasPermissions(context!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openPhotos()
            return
        }
        EasyPermissions.requestPermissions(
            this, resources.getString(R.string.read_extarnal_permission_rationale),
            REQUEST_CODE_READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun openPhotos() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, resources.getString(R.string.chooser_select_picture)),
            REQUEST_CODE_SELECT_IMAGES
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImageUris = ArrayList<Uri?>()
        if (requestCode == REQUEST_CODE_SELECT_IMAGES) {
            if (data?.clipData == null) {
                // user picked only one image
                selectedImageUris.add(data?.data)
            } else {
                // user picked multiple images
                for (i in 0 until data.clipData!!.itemCount) {
                    selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
                }
            }
        }
        if (selectedImageUris.size == 0) {
            return
        }
        val args = Bundle()
        args.putParcelableArrayList(PaperListFragment.ARG_SELECTED_IMAGE_LIST, selectedImageUris)
        Navigation.findNavController(view!!).navigate(R.id.action_paper_wall_to_list, args)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            Toast.makeText(context, R.string.read_extarnal_permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            openPhotos()
        }
    }
}