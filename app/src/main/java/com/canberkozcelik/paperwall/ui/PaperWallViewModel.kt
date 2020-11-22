package com.canberkozcelik.paperwall.ui

import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.syntax.simple.intent
import com.babylon.orbit2.syntax.simple.postSideEffect
import com.babylon.orbit2.syntax.simple.reduce
import com.babylon.orbit2.viewmodel.container
import com.canberkozcelik.paperwall.common.NavigationEvent

class PaperWallViewModel @ViewModelInject constructor(@Assisted savedStateHandle: SavedStateHandle) :
    ViewModel(), ContainerHost<PaperWallState, NavigationEvent> {

    override val container =
        container<PaperWallState, NavigationEvent>(PaperWallState(), savedStateHandle) {
            if (it.selectedImageUris.isNotEmpty()) {
                intent {
                    reduce {
                        state.copy(selectedImageUris = it.selectedImageUris)
                    }
                }
            }
        }

    fun onImageClicked(imageUri: Uri) = intent {
        postSideEffect(OpenPaperNavigationEvent(imageUri))
    }
}
