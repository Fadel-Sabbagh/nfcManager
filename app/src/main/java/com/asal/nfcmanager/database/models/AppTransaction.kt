package com.asal.nfcmanager.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apptransactions")
data class AppTransaction(
    @PrimaryKey @ColumnInfo(name = "transaction_no") val transactionNo: Int,
    @ColumnInfo(name = "balance_no") val balanceNo: Int,
    @ColumnInfo(name = "old_balance_usd") val oldBalanceUSD: Double,
    @ColumnInfo(name = "new_balance_usd") val newBalanceUSD: Double,
    @ColumnInfo(name = "new_balance_eur") val newBalanceEUR: Double,
    @ColumnInfo(name = "exchange_rate") val exchangeRate: Double
)