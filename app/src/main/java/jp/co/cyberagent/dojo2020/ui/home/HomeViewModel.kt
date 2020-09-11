package jp.co.cyberagent.dojo2020.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import jp.co.cyberagent.dojo2020.DI
import jp.co.cyberagent.dojo2020.data.ext.accessWithUid
import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.util.Left
import jp.co.cyberagent.dojo2020.util.Right
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {
    private val memoRepository = DI.injectDefaultMemoRepository(context)
    private val draftRepository = DI.injectDefaultDraftRepository(context)
    private val firebaseUserInfoRepository = DI.injectDefaultUserInfoRepository()

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()
    val userLiveData = userFlow.asLiveData()

    val memoListFlow = flow {
        userFlow.accessWithUid { uid ->
            val rightList = memoRepository
                .fetchAllMemo(uid)
                .map { memoList -> memoList.map { Right(it) } }

            emitAll(rightList)
        }
    }

    val draftListFlow = draftRepository
        .fetchAllDraft()
        .map { draftList -> draftList.map { Left(it) } }

    @FlowPreview
    val textListLiveData = draftListFlow.combine(memoListFlow) { leftList, rightList ->
        Log.d(TAG, "onChange in HomeViewModel")
        leftList + rightList
    }.asLiveData()

    fun filter() = viewModelScope.launch {
        userFlow.collect { userInfo ->
            memoRepository.fetchAllMemo(userInfo?.uid).collect { memoList ->
                memoList.filter { it.category == "kotlin" }
            }
        }
    }

    fun saveMemo(memo: Memo) = viewModelScope.launch {
        userFlow.accessWithUid { uid ->
            memoRepository.saveMemo(uid, memo)
            Log.d(TAG, "uid is ${uid}")
        }
    }

    fun deleteDraft(draft: Draft) = viewModelScope.launch {
        draftRepository.deleteDraftById(draft.id)
    }
}