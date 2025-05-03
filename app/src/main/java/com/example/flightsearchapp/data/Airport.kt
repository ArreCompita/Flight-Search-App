package com.example.flightsearchapp.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "iata_code") val iataCode: String,
    @ColumnInfo(name = "name") val airportName: String,
    @ColumnInfo(name = "passengers") val passengers: Int
)

@Entity(tableName = "favorite")
data class FavoriteRoute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "departure_code") val departureCode: String,
    @ColumnInfo(name = "destination_code") val destinationCode: String
)