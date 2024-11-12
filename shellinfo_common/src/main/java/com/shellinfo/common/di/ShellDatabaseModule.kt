package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.code.DatabaseCall
import com.shellinfo.common.data.local.db.DatabaseConfig
import com.shellinfo.common.data.local.db.dao.DailyLimitDao
import com.shellinfo.common.data.local.db.dao.OrderDao
import com.shellinfo.common.data.local.db.dao.PassDao
import com.shellinfo.common.data.local.db.dao.StationsDao
import com.shellinfo.common.data.local.db.dao.TicketBackupDao
import com.shellinfo.common.data.local.db.dao.TripLimitDao
import com.shellinfo.common.data.local.db.dao.ZoneDao
import com.shellinfo.common.data.local.db.repository.DbRepository
import com.shellinfo.common.data.shared.SharedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ShellDatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): DatabaseConfig {
        return DatabaseConfig.getDatabase(appContext)
    }


    @Provides
    fun provideStationsDao(database: DatabaseConfig): StationsDao {
        return database.stationsDao()
    }

    @Provides
    fun provideOrderDao(database: DatabaseConfig): OrderDao {
        return database.orderDao()
    }

    @Provides
    fun provideTicketBackupDao(database: DatabaseConfig): TicketBackupDao {
        return database.ticketBackupDao()
    }

    @Provides
    fun providePassDao(database: DatabaseConfig):PassDao{
        return database.passDao()
    }

    @Provides
    fun provideDailyLimitDao(database: DatabaseConfig):DailyLimitDao{
        return database.dailyLimitDao()
    }

    @Provides
    fun provideTripLimitDao(database: DatabaseConfig):TripLimitDao{
        return database.tripLimitDao()
    }

    @Provides
    fun provideZoneDao(database: DatabaseConfig):ZoneDao{
        return database.zoneDao()
    }

    @Provides
    @Singleton
    fun provideDbRepository(stationsDao: StationsDao, orderDao: OrderDao, ticketBackupDao: TicketBackupDao, passDao: PassDao,
                            dailyLimitDao: DailyLimitDao, tripLimitDao: TripLimitDao,zoneDao: ZoneDao
    ): DbRepository  {
        return DbRepository(stationsDao,orderDao, ticketBackupDao,passDao,dailyLimitDao,tripLimitDao,zoneDao)
    }



    @Singleton
    @Provides
    fun provideDatabaseCall(dbRepository: DbRepository,sharedDataManager: SharedDataManager) : DatabaseCall =
        DatabaseCall(dbRepository,sharedDataManager)
}