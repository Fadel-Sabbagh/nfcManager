package com.asal.nfcmanager.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.asal.nfcmanager.database.models.AppTransaction


@Dao
interface AppTransactionDao {
    @Query("SELECT * FROM apptransactions")
    fun getAll(): List<AppTransaction>

    @Query("SELECT * FROM apptransactions WHERE transaction_no IN (:transactionIds)")
    fun loadAllByIds(transactionIds: IntArray): List<AppTransaction>

    @Insert
    fun insertAll(vararg transactions: AppTransaction)

    @Delete
    fun delete(transaction: AppTransaction)
}