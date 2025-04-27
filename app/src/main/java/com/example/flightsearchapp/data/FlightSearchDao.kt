package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FlightSearchDao {
    @Query(
        """SELECT * FROM airport 
           WHERE iata_code LIKE '%' || :searchQuery || '%' or name LIKE '%' || :searchQuery || '%' """
    )
    fun searchAirport(searchQuery: String): Flow<List<Airport>>
    @Query(
        """SELECT * FROM airport 
           WHERE iata_code = :iataCode LIMIT 1"""
    )
    fun getAirportByIataCode(iataCode: String): Flow<Airport?>

    @Query(
        """SELECT * FROM airport 
           ORDER BY passengers ASC"""
    )
    fun getAllAirports(): Flow<List<Airport>>
    @Query(
        "SELECT * FROM favorite "
    )
    fun getFavoriteRoutes(): Flow<List<FavoriteRoute>>
    @Insert
    suspend fun insertFavoriteRoute(favoriteRoute: FavoriteRoute)
    @Delete
    suspend fun deleteFavoriteRoute(favoriteRoute: FavoriteRoute)
    @Insert
    suspend fun insertAirport(airport: Airport) // For testing purposes

}