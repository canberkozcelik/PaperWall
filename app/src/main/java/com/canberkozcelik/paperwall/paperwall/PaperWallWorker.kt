package com.canberkozcelik.paperwall.paperwall

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.canberkozcelik.paperwall.helper.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by canberkozcelik on 27.03.2019.
 */
class PaperWallWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object {
        const val KEY_IMAGE_STRINGS: String = "KEY_IMAGE_STRINGS"
        const val KEY_DATA_INT_SELECTED_INDEX: String = "KEY_DATA_INT_SELECTED_INDEX"
    }

    override fun doWork(): Result {
        return try {
            val appContext = applicationContext
            val selectedImagesStr = inputData.getStringArray(KEY_IMAGE_STRINGS)
            var selectedIndex = SharedPrefHelper(appContext)
                .getDataInt(KEY_DATA_INT_SELECTED_INDEX, 0)
            selectedIndex++
            selectedIndex = (selectedIndex == selectedImagesStr?.size).let { 0 }
            SharedPrefHelper(appContext)
                .putData(KEY_DATA_INT_SELECTED_INDEX, selectedIndex)
            Glide.with(appContext)
                .asBitmap()
                .load(selectedImagesStr?.get(selectedIndex))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        setWallpaper(appContext, resource)
                    }
                })
            val outputData = Data.Builder().putStringArray(KEY_IMAGE_STRINGS, selectedImagesStr!!).build()
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            return Result.failure()
        }
    }

    private fun setWallpaper(context: Context, resource: Bitmap) {
        val manager = WallpaperManager.getInstance(context)
        GlobalScope.launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (manager.isWallpaperSupported && manager.isSetWallpaperAllowed) {
                    manager.setBitmap(resource)
                }
            } else {
                manager.setBitmap(resource)
            }
        }
    }
}