package jp.co.cyberagent.dojo2020.data.local.db.memo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memoEntity: MemoEntity)

    @Query(value = "SELECT * FROM memos")
    fun fetchAll(): Flow<List<MemoEntity>>

    @Query(value = "SELECT * FROM memos WHERE id = :id")
    fun fetch(id: String): Flow<MemoEntity?>

    @Query("DELETE FROM memos WHERE id = :id")
    suspend fun deleteById(id: String)
}