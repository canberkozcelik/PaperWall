package com.canberkozcelik.paperwall.helper

import android.os.SystemClock
import android.view.View

/**
 * Created by canberkozcelik on 2019-08-04.
 */
class SafeClickListener(private var defaultInterval: Int = 1000, private val onSafeClick: (View?) -> Unit) :
    View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < defaultInterval) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        onSafeClick(v)
    }
}

fun View.setOnSafeClickListener(onSafeClick: (View?) -> Unit) {

    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}