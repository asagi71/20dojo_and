package jp.co.cyberagent.dojo2020.test

import jp.co.cyberagent.dojo2020.data.model.Memo
import java.util.*
import kotlin.random.Random

object MemoData {
    val categoryList = listOf(
        "None", "Android", "Redux", "Kotlin", "iOS"
    )

    val memoList = MutableList(15) { createTestMemo() }

    private fun createTestMemo() = Memo(
        generateUUID(), "title", "contents", Random.nextLong(60), pickUpCategory()
    )

    private fun pickUpCategory(): String = categoryList.random()

    private fun generateUUID(): String = UUID.randomUUID().toString()
}
