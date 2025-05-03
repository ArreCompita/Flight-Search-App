package com.example.flightsearchapp.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FlightSearchDao_Impl(
  __db: RoomDatabase,
) : FlightSearchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteRoute: EntityInsertAdapter<FavoriteRoute>

  private val __insertAdapterOfAirport: EntityInsertAdapter<Airport>

  private val __deleteAdapterOfFavoriteRoute: EntityDeleteOrUpdateAdapter<FavoriteRoute>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteRoute = object : EntityInsertAdapter<FavoriteRoute>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `favorite` (`id`,`departure_code`,`destination_code`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteRoute) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.departureCode)
        statement.bindText(3, entity.destinationCode)
      }
    }
    this.__insertAdapterOfAirport = object : EntityInsertAdapter<Airport>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `airport` (`id`,`iata_code`,`name`,`passengers`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Airport) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.iataCode)
        statement.bindText(3, entity.airportName)
        statement.bindLong(4, entity.passengers.toLong())
      }
    }
    this.__deleteAdapterOfFavoriteRoute = object : EntityDeleteOrUpdateAdapter<FavoriteRoute>() {
      protected override fun createQuery(): String = "DELETE FROM `favorite` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteRoute) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertFavoriteRoute(favoriteRoute: FavoriteRoute): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFavoriteRoute.insert(_connection, favoriteRoute)
  }

  public override suspend fun insertAirport(airport: Airport): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfAirport.insert(_connection, airport)
  }

  public override suspend fun deleteFavoriteRoute(favoriteRoute: FavoriteRoute): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfFavoriteRoute.handle(_connection, favoriteRoute)
  }

  public override fun searchAirport(searchQuery: String): Flow<List<Airport>> {
    val _sql: String = """
        |SELECT * FROM airport 
        |           WHERE iata_code LIKE '%' || ? || '%' or name LIKE '%' || ? || '%' 
        """.trimMargin()
    return createFlow(__db, false, arrayOf("airport")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, searchQuery)
        _argIndex = 2
        _stmt.bindText(_argIndex, searchQuery)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfIataCode: Int = getColumnIndexOrThrow(_stmt, "iata_code")
        val _cursorIndexOfAirportName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPassengers: Int = getColumnIndexOrThrow(_stmt, "passengers")
        val _result: MutableList<Airport> = mutableListOf()
        while (_stmt.step()) {
          val _item: Airport
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpIataCode: String
          _tmpIataCode = _stmt.getText(_cursorIndexOfIataCode)
          val _tmpAirportName: String
          _tmpAirportName = _stmt.getText(_cursorIndexOfAirportName)
          val _tmpPassengers: Int
          _tmpPassengers = _stmt.getLong(_cursorIndexOfPassengers).toInt()
          _item = Airport(_tmpId,_tmpIataCode,_tmpAirportName,_tmpPassengers)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAirportByIataCode(iataCode: String): Flow<Airport?> {
    val _sql: String = """
        |SELECT * FROM airport 
        |           WHERE iata_code = ? LIMIT 1
        """.trimMargin()
    return createFlow(__db, false, arrayOf("airport")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, iataCode)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfIataCode: Int = getColumnIndexOrThrow(_stmt, "iata_code")
        val _cursorIndexOfAirportName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPassengers: Int = getColumnIndexOrThrow(_stmt, "passengers")
        val _result: Airport?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpIataCode: String
          _tmpIataCode = _stmt.getText(_cursorIndexOfIataCode)
          val _tmpAirportName: String
          _tmpAirportName = _stmt.getText(_cursorIndexOfAirportName)
          val _tmpPassengers: Int
          _tmpPassengers = _stmt.getLong(_cursorIndexOfPassengers).toInt()
          _result = Airport(_tmpId,_tmpIataCode,_tmpAirportName,_tmpPassengers)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllAirports(): Flow<List<Airport>> {
    val _sql: String = """
        |SELECT * FROM airport 
        |           ORDER BY passengers ASC
        """.trimMargin()
    return createFlow(__db, false, arrayOf("airport")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfIataCode: Int = getColumnIndexOrThrow(_stmt, "iata_code")
        val _cursorIndexOfAirportName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPassengers: Int = getColumnIndexOrThrow(_stmt, "passengers")
        val _result: MutableList<Airport> = mutableListOf()
        while (_stmt.step()) {
          val _item: Airport
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpIataCode: String
          _tmpIataCode = _stmt.getText(_cursorIndexOfIataCode)
          val _tmpAirportName: String
          _tmpAirportName = _stmt.getText(_cursorIndexOfAirportName)
          val _tmpPassengers: Int
          _tmpPassengers = _stmt.getLong(_cursorIndexOfPassengers).toInt()
          _item = Airport(_tmpId,_tmpIataCode,_tmpAirportName,_tmpPassengers)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFavoriteRoutes(): Flow<List<FavoriteRoute>> {
    val _sql: String = "SELECT * FROM favorite "
    return createFlow(__db, false, arrayOf("favorite")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfDepartureCode: Int = getColumnIndexOrThrow(_stmt, "departure_code")
        val _cursorIndexOfDestinationCode: Int = getColumnIndexOrThrow(_stmt, "destination_code")
        val _result: MutableList<FavoriteRoute> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteRoute
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpDepartureCode: String
          _tmpDepartureCode = _stmt.getText(_cursorIndexOfDepartureCode)
          val _tmpDestinationCode: String
          _tmpDestinationCode = _stmt.getText(_cursorIndexOfDestinationCode)
          _item = FavoriteRoute(_tmpId,_tmpDepartureCode,_tmpDestinationCode)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFavoriteRouteByIataCodes(deapartureIataCode: String,
      arrivalIataCode: String): Flow<FavoriteRoute?> {
    val _sql: String = """
        |SELECT * FROM favorite 
        |           WHERE departure_code = ? AND destination_code = ? LIMIT 1
        """.trimMargin()
    return createFlow(__db, false, arrayOf("favorite")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deapartureIataCode)
        _argIndex = 2
        _stmt.bindText(_argIndex, arrivalIataCode)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfDepartureCode: Int = getColumnIndexOrThrow(_stmt, "departure_code")
        val _cursorIndexOfDestinationCode: Int = getColumnIndexOrThrow(_stmt, "destination_code")
        val _result: FavoriteRoute?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpDepartureCode: String
          _tmpDepartureCode = _stmt.getText(_cursorIndexOfDepartureCode)
          val _tmpDestinationCode: String
          _tmpDestinationCode = _stmt.getText(_cursorIndexOfDestinationCode)
          _result = FavoriteRoute(_tmpId,_tmpDepartureCode,_tmpDestinationCode)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
