package jp.co.cyberagent.dojo2020.ui.edit

import android.content.Context
import androidx.lifecycle.*
import jp.co.cyberagent.dojo2020.DI
import jp.co.cyberagent.dojo2020.data.ext.accessWithUid
import jp.co.cyberagent.dojo2020.data.model.Draft
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class MemoEditViewModel(context: Context) : ViewModel() {
    private val draftRepository = DI.injectDefaultDraftRepository(context)
    private val categoryRepository = DI.injectDefaultCategoryRepository(context)
    private val userInfoRepository = DI.injectDefaultUserInfoRepository()

    private val userFlow = userInfoRepository.fetchUserInfo()
    val userLiveData = userFlow.asLiveData()

    val draftListLiveData = liveData<List<Draft>> {
        emitSource(draftRepository.fetchAllDraft().asLiveData())
    }

    val categoryListLiveData = liveData {
        userFlow.accessWithUid { uid ->
            val categoryListFlow = categoryRepository.fetchAllCategory(uid)
            val categorySetFlow = categoryListFlow.map { it.toSet() }

            emitSource(categorySetFlow.asLiveData())
        }
    }

    private val memoInfoMutableLivaData = MutableLiveData<Draft>()
    val memoInfoLiveData: LiveData<Draft> = memoInfoMutableLivaData

    // id で選択されたメモの情報を持ってくる
    fun fetchMemo(id: String) = viewModelScope.launch {
        draftRepository.fetchDraftById(id).collect {
            memoInfoMutableLivaData.value = it
        }
    }

    fun addDraft(title: String, content: String, category: String) = viewModelScope.launch {
        val id = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        val draft = Draft(id, title, content, startTime, category)

        draftRepository.saveDraft(draft)
    }

    fun addCategory(categoryName: String) = viewModelScope.launch {

        userFlow.accessWithUid { uid -> categoryRepository.saveCategory(uid, categoryName) }
    }
}
