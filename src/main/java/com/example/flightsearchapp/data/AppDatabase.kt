package com.example.flightsearchapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Airport::class, FavoriteRoute::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun flightSearchDao(): FlightSearchDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flight_search_database"
                ).createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigration(true)
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this project.
                    .build()
                    .also {
                    INSTANCE = it
                }
            }
        }
    }

}