package jp.co.cyberagent.dojo2020.test

import jp.co.cyberagent.dojo2020.data.CategoryRepository
import kotlinx.coroutines.flow.flow

object TestCategoryRepository : CategoryRepository {
    override suspend fun saveCategory(uid: String?, category: String) {

    }

    override suspend fun fetchAllCategory(uid: String?) = flow<List<String>> {
        emit(MemoData.categoryList)
    }

    override suspend fun deleteCategory(uid: String?, category: String) {

    }
}