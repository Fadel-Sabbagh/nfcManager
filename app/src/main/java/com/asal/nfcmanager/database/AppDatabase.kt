package com.asal.nfcmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asal.nfcmanager.database.daos.AppTransactionDao
import com.asal.nfcmanager.database.models.AppTransaction

@Database(entities = arrayOf(AppTransaction::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): AppTransactionDao
}