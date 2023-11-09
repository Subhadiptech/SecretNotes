package com.ersubhadip.secretnotes

import android.app.Application
import androidx.room.Room
import com.ersubhadip.secretnotes.database.SecretDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SecretApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                module {
                    single {
                        Room.databaseBuilder(this@SecretApplication,SecretDatabase::class.java,"secret").build()
                    }
                }
            )
        }
    }
}