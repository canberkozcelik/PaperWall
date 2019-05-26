package com.canberkozcelik.paperwall

import android.content.Intent
import android.net.Uri
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by canberkozcelik on 2019-05-26.
 */
class PaperWallPresenter(var paperWallView: PaperWallContract.View) : PaperWallContract.Presenter,
    EasyPermissions.PermissionCallbacks {

    init {
        paperWallView.presenter = this
    }

    override fun start() {
        paperWallView.showLanding()
    }

    override fun initPermissions() {
        if (EasyPermissions.hasPermissions(paperWallView.getCxt(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openPhotos()
            return
        }
        EasyPermissions.requestPermissions(
            paperWallView.getFragment(),
            paperWallView.getFragment().resources.getString(R.string.read_extarnal_permission_rationale),
            PaperWallFragment.REQUEST_CODE_READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun openPhotos() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        paperWallView.getFragment().startActivityForResult(
            Intent.createChooser(
                intent,
                paperWallView.getFragment().resources.getString(R.string.chooser_select_picture)
            ),
            PaperWallFragment.REQUEST_CODE_SELECT_IMAGES
        )
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        val selectedImageUris = ArrayList<Uri?>()
        if (requestCode == PaperWallFragment.REQUEST_CODE_SELECT_IMAGES) {
            if (data == null) {
                return
            }
            if (data.clipData == null) {
                // user picked only one image
                selectedImageUris.add(data.data)
            } else {
                // user picked multiple images
                for (i in 0 until data.clipData!!.itemCount) {
                    selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
                }
            }
            if (selectedImageUris.size == 0) {
                return
            }
            paperWallView.navigateToPaperList(selectedImageUris)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PaperWallFragment.REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            paperWallView.showPermissionDenied()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PaperWallFragment.REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            openPhotos()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}