package jp.co.cyberagent.dojo2020.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.github.mikephil.charting.data.PieEntry
import jp.co.cyberagent.dojo2020.DI
import jp.co.cyberagent.dojo2020.data.ext.accessWithUid
import jp.co.cyberagent.dojo2020.data.model.Profile
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(context: Context) : ViewModel() {

    private val firebaseUserInfoRepository = DI.injectDefaultUserInfoRepository()
    private val firebaseProfileRepository = DI.injectTestProfileRepository()
    private val memoRepository = DI.injectTestMemoRepository()
    private val categoryRepository = DI.injectTestCategoryRepository()

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()
    val firebaseUserInfo = userFlow.asLiveData()

    val profileLiveData: LiveData<Profile?> = liveData {
        userFlow.accessWithUid { uid ->
            emitSource(firebaseProfileRepository.fetchProfile(uid).onEach {
                it ?: return@onEach
                firebaseProfileRepository.saveProfile(uid, it)
            }.asLiveData())
        }
    }


    val totalTimeLiveData = liveData {
        userFlow.accessWithUid { uid ->
            emitSource(memoRepository.fetchAllMemo(uid).map {
                it.fold(0L) { result, memo -> result + memo.time }.toInt()
            }.asLiveData())
        }
    }

    val pieEntryListLiveData: LiveData<List<PieEntry>> = liveData {
        userFlow.accessWithUid { uid ->
            val categoryListFlow = categoryRepository.fetchAllCategory(uid)
            val memoListFlow = memoRepository.fetchAllMemo(uid)

            val entryList = categoryListFlow.combine(memoListFlow) { categoryList, memoList ->
                categoryList.map { category ->
                    val totalTimeForEachCategory = memoList
                        .filter { it.category == category }
                        .fold(0L) { result, memo -> result + memo.time }

                    PieEntry(totalTimeForEachCategory.toFloat(), category)
                }
            }

            emitSource(entryList.asLiveData())
        }
    }

}