package com.shellinfo.common.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shellinfo.common.data.local.db.dao.DailyLimitDao
import com.shellinfo.common.data.local.db.dao.OrderDao
import com.shellinfo.common.data.local.db.dao.PassDao
import com.shellinfo.common.data.local.db.dao.PurchasePassDao
import com.shellinfo.common.data.local.db.dao.StationsDao
import com.shellinfo.common.data.local.db.dao.TicketBackupDao
import com.shellinfo.common.data.local.db.dao.TripLimitDao
import com.shellinfo.common.data.local.db.dao.ZoneDao
import com.shellinfo.common.data.local.db.entity.DailyLimitTable
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.PurchasePassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable
import com.shellinfo.common.data.local.db.entity.TripLimitTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import com.shellinfo.common.utils.DBConstants

@Database(entities = [StationsTable::class,OrdersTable::class,TicketBackupTable::class, PassTable::class,
                     DailyLimitTable::class,TripLimitTable::class,ZoneTable::class,PurchasePassTable::class] , version = 7, exportSchema = false)
abstract class DatabaseConfig : RoomDatabase(){

    abstract fun stationsDao() : StationsDao
    abstract fun orderDao() : OrderDao
    abstract fun ticketBackupDao() : TicketBackupDao
    abstract fun passDao():PassDao
    abstract fun dailyLimitDao():DailyLimitDao
    abstract fun tripLimitDao():TripLimitDao
    abstract fun zoneDao():ZoneDao
    abstract fun purchasePassDao():PurchasePassDao

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