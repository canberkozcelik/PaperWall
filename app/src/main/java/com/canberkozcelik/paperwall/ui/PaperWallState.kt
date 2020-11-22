package com.canberkozcelik.paperwall.ui

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaperWallState(val selectedImageUris: List<Uri> = emptyList()) : Parcelable
