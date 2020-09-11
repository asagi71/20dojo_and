package jp.co.cyberagent.dojo2020.util

import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo

sealed class Text
data class Left(val value: Draft) : Text()
data class Right(val value: Memo) : Text()