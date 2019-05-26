package com.canberkozcelik.paperwall

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

/**
 * Created by canberkozcelik on 2019-05-26.
 */
class PaperWallContract {

    interface View : BaseView<Presenter> {

        fun getFragment() : Fragment

        fun getCxt() : Context

        fun navigateToPaperList(selectedImageUris: ArrayList<Uri?>)

        fun showPermissionDenied()

        fun showLanding()
    }

    interface Presenter : BasePresenter {

        fun initPermissions()

        fun openPhotos()

        fun result(requestCode: Int, resultCode: Int, data: Intent?)
    }
}