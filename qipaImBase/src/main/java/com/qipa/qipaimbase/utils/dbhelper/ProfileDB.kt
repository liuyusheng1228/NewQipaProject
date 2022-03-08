package com.qipa.qipaimbase.utils.dbhelper

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Profile::class], version = 2, exportSchema = false)
abstract class ProfileDB() : RoomDatabase() {
    abstract fun profileDao(): ProfileDao?

    companion object {
        private val DB_NAME = "profile.db"

        @Volatile
        private var instance: ProfileDB? = null
        @Synchronized
        fun getInstance(context: Context): ProfileDB? {
            if (instance == null) {
                instance = create(context)
            }
            return instance
        }

        private fun create(context: Context): ProfileDB {
            return Room.databaseBuilder(
                context,
                ProfileDB::class.java,
                DB_NAME
            ).addMigrations(migration1To2).build()
        }

        private val migration1To2: Migration = object : Migration(1, 2) {
            override fun migrate(@NonNull database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE profile "
                            + " ADD COLUMN name TEXT"
                )
            }
        }
    }
}
