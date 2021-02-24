package com.canberkozcelik.paperwall.paperwall

import android.content.Context
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
import com.canberkozcelik.paperwall.R
import com.canberkozcelik.paperwall.databinding.FragmentPaperWallBinding
import com.canberkozcelik.paperwall.helper.setOnSafeClickListener
import com.canberkozcelik.paperwall.paperlist.PaperListFragment
import timber.log.Timber


class PaperWallFragment : Fragment(), PaperWallContract.View {

    companion object PaperListConstants {
        const val REQUEST_CODE_READ_EXTERNAL_STORAGE: Int = 1123
        const val REQUEST_CODE_SELECT_IMAGES: Int = 5813
    }

    override lateinit var presenter: PaperWallContract.Presenter

    private lateinit var binding: FragmentPaperWallBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_paper_wall, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = PaperWallPresenter(this)
        binding.root.setOnSafeClickListener { presenter.initPermissions() }
    }

    override fun showLanding() {
        binding.rootLayout.visibility = View.VISIBLE
    }

    override fun getFragment(): Fragment {
        return this
    }

    override fun getCxt(): Context {
        return context!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.result(requestCode, resultCode, data)
    }

    override fun navigateToPaperList(selectedImageUris: ArrayList<Uri?>) {
        try {
            val args = Bundle()
            args.putParcelableArrayList(PaperListFragment.ARG_SELECTED_IMAGE_LIST, selectedImageUris)
            Navigation.findNavController(view!!).navigate(R.id.action_paper_wall_to_list, args)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun showPermissionDenied() {
        Toast.makeText(context, R.string.read_extarnal_permission_denied, Toast.LENGTH_LONG).show()
    }
}