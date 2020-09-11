package jp.co.cyberagent.dojo2020.test

import jp.co.cyberagent.dojo2020.data.MemoRepository
import jp.co.cyberagent.dojo2020.data.model.Memo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object TestMemoRepository : MemoRepository {
    override suspend fun saveMemo(uid: String?, memo: Memo) {

    }

    override suspend fun fetchAllMemo(uid: String?): Flow<List<Memo>> = flow {
        emit(MemoData.memoList)
    }

    override suspend fun fetchFilteredMemoByCategory(
        uid: String?,
        category: String
    ) = flow<List<Memo>> {

    }

    override suspend fun fetchMemoById(uid: String?, id: String) = flow<Memo?> {

    }

    override suspend fun deleteMemoById(uid: String?, id: String) {

    }
}
