package jp.co.cyberagent.dojo2020.ui.create

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import jp.co.cyberagent.dojo2020.ui.edit.MemoEditViewModel

@Suppress("UNCHECKED_CAST")
class MemoEditViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    private val context: Context
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = MemoEditViewModel(context) as T
}