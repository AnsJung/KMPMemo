package com.example.kmp_memo.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.kmp_memo.data.database.MemoDatabase
import com.example.kmp_memo.data.mapper.toMemo
import com.example.kmp_memo.data.model.Memo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class SqlDelightMemoRepository(
    private val database: MemoDatabase,
    private val ioDispatcher: CoroutineDispatcher,
    private val currentTimeMillis: () -> Long = {
        Clock.System.now().toEpochMilliseconds()
    },
) : MemoRepository {

    private val memoQueries = database.memoQueries

    override fun observeMemos(): Flow<List<Memo>> {
        return memoQueries
            .selectAll()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows ->
                rows.map { row -> row.toMemo() }
            }
    }

    override suspend fun getMemo(id: Long): Memo? {
        return withContext(ioDispatcher) {
            memoQueries
                .selectById(id)
                .executeAsOneOrNull()
                ?.toMemo()
        }
    }

    override suspend fun createMemo(
        title: String,
        content: String,
    ): Long {
        return withContext(ioDispatcher) {
            database.transactionWithResult {
                memoQueries.insert(
                    title = title,
                    content = content,
                    createdAt = currentTimeMillis(),
                )
                memoQueries.lastInsertedId().executeAsOne()
            }
        }
    }

    override suspend fun updateMemo(
        id: Long,
        title: String,
        content: String,
    ) {
        withContext(ioDispatcher) {
            memoQueries.update(
                id = id,
                title = title,
                content = content,
                updatedAt = currentTimeMillis(),
            )
        }
    }

    override suspend fun deleteMemo(id: Long) {
        withContext(ioDispatcher) {
            memoQueries.deleteById(id)
        }
    }
}