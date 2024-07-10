package com.shellinfo.common.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shellinfo.common.data.local.db.dao.OrderDao
import com.shellinfo.common.data.local.db.dao.StationsDao
import com.shellinfo.common.data.local.db.dao.TicketBackupDao
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable
import com.shellinfo.common.utils.DBConstants

@Database(entities = [StationsTable::class,OrdersTable::class,TicketBackupTable::class] , version = 1, exportSchema = false)
abstract class DatabaseConfig : RoomDatabase(){

    abstract fun stationsDao() : StationsDao
    abstract fun orderDao() : OrderDao
    abstract fun ticketBackupDao() : TicketBackupDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseConfig? = null

        fun getDatabase(context: Context): DatabaseConfig {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseConfig::class.java,
                    DBConstants.DATABASE_FILE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}