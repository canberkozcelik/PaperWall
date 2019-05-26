package com.canberkozcelik.paperwall

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.work.*
import com.canberkozcelik.paperwall.databinding.FragmentPaperListBinding
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by canberkozcelik on 24.03.2019.
 */
class PaperListFragment : Fragment() {

    companion object {
        const val ARG_SELECTED_IMAGE_LIST: String = "ARG_SELECTED_IMAGE_LIST"
        const val KEY_IMAGE_STRINGS: String = "KEY_IMAGE_STRINGS"
        const val TAG_SET_WP_ONE_TIME: String = "TAG_SET_WP_ONE_TIME"
        const val TAG_SET_WP_PERIODIC: String = "TAG_SET_WP_PERIODIC"
    }

    private lateinit var binding: FragmentPaperListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_paper_list, container, false)
        return binding.root
    }

    private lateinit var paperListAdapter: RecyclerView.Adapter<*>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val workManager: WorkManager = WorkManager.getInstance()
        val selectedImageUris = arguments?.getParcelableArrayList<Uri>(ARG_SELECTED_IMAGE_LIST)
        paperListAdapter = PaperListAdapter(selectedImageUris)
        binding.selectedPaperList.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = paperListAdapter
        }

        binding.fabDone.setOnClickListener {
            binding.loading.show()
            enqueueWorkManager(workManager, selectedImageUris)
        }
    }

    private fun enqueueWorkManager(workManager: WorkManager, selectedImageUris: ArrayList<Uri>?) {
        workManager.getWorkInfosByTagLiveData(TAG_SET_WP_ONE_TIME).observe(this, oneTimeWorkInfoObserver())
        workManager.getWorkInfosByTagLiveData(TAG_SET_WP_PERIODIC).observe(this, periodicWorkInfoObserver())
        val setWpOneTimeRequest = OneTimeWorkRequestBuilder<PaperWallWorker>()
            .addTag(TAG_SET_WP_ONE_TIME)
            .setInputData(createInputDataForUris(selectedImageUris))
            .build()
        workManager.enqueueUniqueWork("setWallpaperOneTimeWork", ExistingWorkPolicy.REPLACE, setWpOneTimeRequest)
        if (selectedImageUris?.size == 1) {
            return
        }
        val setWpPeriodicRequest = PeriodicWorkRequestBuilder<PaperWallWorker>(24, TimeUnit.HOURS)
            .setConstraints(buildConstraints())
            .addTag(TAG_SET_WP_PERIODIC)
            .setInputData(createInputDataForUris(selectedImageUris))
            .build()
        workManager.enqueueUniquePeriodicWork(
            "setWallpaperPeriodicWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            setWpPeriodicRequest
        )
    }

    private fun periodicWorkInfoObserver(): Observer<in MutableList<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }
            val workInfo = listOfWorkInfo[0]
            if (workInfo.state == WorkInfo.State.ENQUEUED) {
                showWorkScheduled()
            }
        }
    }

    private fun showWorkScheduled() {
        fragmentManager?.popBackStack()
        Navigation.findNavController(view!!).navigate(R.id.action_paper_list_to_success)
    }

    private fun buildConstraints(): Constraints {
        val constraintsBuilder = Constraints.Builder()
        constraintsBuilder.setRequiresStorageNotLow(true)
        constraintsBuilder.setRequiresBatteryNotLow(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            constraintsBuilder.setRequiresDeviceIdle(true)
        }
        return constraintsBuilder.build()
    }

    private fun oneTimeWorkInfoObserver(): Observer<in List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }
            val workInfo = listOfWorkInfo[0]
            if (workInfo.state.isFinished) {
                binding.loading.hide()
            }
        }
    }

    private fun createInputDataForUris(selectedImageUris: ArrayList<Uri>?): Data {
        val builder = Data.Builder()
        val selectedImagesStr = ArrayList<String>()
        selectedImageUris?.let {
            selectedImageUris.forEach { t: Uri? ->
                selectedImagesStr.add(t.toString())
            }
            builder.putStringArray(KEY_IMAGE_STRINGS, selectedImagesStr.toTypedArray())
        }
        return builder.build()
    }
}