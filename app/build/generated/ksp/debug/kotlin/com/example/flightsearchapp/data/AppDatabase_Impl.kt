package com.example.flightsearchapp.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _flightSearchDao: Lazy<FlightSearchDao> = lazy {
    FlightSearchDao_Impl(this)
  }


  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "574d3ad56019e22f46027571157141e4", "2a8812acb58dde16bd325311d13c79ff") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `airport` (`id` INTEGER NOT NULL, `iata_code` TEXT NOT NULL, `name` TEXT NOT NULL, `passengers` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorite` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `departure_code` TEXT NOT NULL, `destination_code` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '574d3ad56019e22f46027571157141e4')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `airport`")
        connection.execSQL("DROP TABLE IF EXISTS `favorite`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsAirport: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAirport.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAirport.put("iata_code", TableInfo.Column("iata_code", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAirport.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAirport.put("passengers", TableInfo.Column("passengers", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAirport: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAirport: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAirport: TableInfo = TableInfo("airport", _columnsAirport, _foreignKeysAirport,
            _indicesAirport)
        val _existingAirport: TableInfo = read(connection, "airport")
        if (!_infoAirport.equals(_existingAirport)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |airport(com.example.flightsearchapp.data.Airport).
              | Expected:
              |""".trimMargin() + _infoAirport + """
              |
              | Found:
              |""".trimMargin() + _existingAirport)
        }
        val _columnsFavorite: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavorite.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorite.put("departure_code", TableInfo.Column("departure_code", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorite.put("destination_code", TableInfo.Column("destination_code", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavorite: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavorite: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavorite: TableInfo = TableInfo("favorite", _columnsFavorite, _foreignKeysFavorite,
            _indicesFavorite)
        val _existingFavorite: TableInfo = read(connection, "favorite")
        if (!_infoFavorite.equals(_existingFavorite)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorite(com.example.flightsearchapp.data.FavoriteRoute).
              | Expected:
              |""".trimMargin() + _infoFavorite + """
              |
              | Found:
              |""".trimMargin() + _existingFavorite)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "airport", "favorite")
  }

  public override fun clearAllTables() {
    super.performClear(false, "airport", "favorite")
  }

  protected override fun getRequiredTypeConverterClasses():
      Map<KClass<out Any>, List<KClass<out Any>>> {
    val _typeConvertersMap: MutableMap<KClass<out Any>, List<KClass<out Any>>> = mutableMapOf()
    _typeConvertersMap.put(FlightSearchDao::class, FlightSearchDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun flightSearchDao(): FlightSearchDao = _flightSearchDao.value
}
