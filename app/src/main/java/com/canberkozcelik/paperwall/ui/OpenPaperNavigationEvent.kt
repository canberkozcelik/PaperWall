package com.canberkozcelik.paperwall.ui

import android.net.Uri
import com.canberkozcelik.paperwall.common.NavigationEvent

data class OpenPaperNavigationEvent(val uri: Uri) : NavigationEvent