package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.code.DatabaseCall
import com.shellinfo.common.data.local.db.DatabaseConfig
import com.shellinfo.common.data.local.db.dao.OrderDao
import com.shellinfo.common.data.local.db.dao.StationsDao
import com.shellinfo.common.data.local.db.dao.TicketBackupDao
import com.shellinfo.common.data.local.db.repository.DbRepository
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
    @Singleton
    fun provideDbRepository(stationsDao: StationsDao, orderDao: OrderDao, ticketBackupDao: TicketBackupDao): DbRepository  {
        return DbRepository(stationsDao,orderDao, ticketBackupDao)
    }



    @Singleton
    @Provides
    fun provideDatabaseCall(dbRepository: DbRepository) : DatabaseCall =
        DatabaseCall(dbRepository)
}