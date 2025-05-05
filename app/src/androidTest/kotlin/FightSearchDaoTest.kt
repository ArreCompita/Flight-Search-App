import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.AppDatabase
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.data.FlightSearchDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FightSearchDaoTest {
    private lateinit var flightSearchDao: FlightSearchDao
    private lateinit var appDatabase: AppDatabase

    private val airportA = Airport(1, "AAA", "Airport A", 100)
    private val airportB = Airport(2, "BBB", "Airport B", 200)
    private val airportC = Airport(3, "CCC", "Airport C", 300)

    private val favoriteRoute0 = FavoriteRoute(0, "AAA", "BBB")
    private val favoriteRoute1 = FavoriteRoute(1, "AAA", "BBB")
    private val favoriteRoute2 = FavoriteRoute(2, "BBB", "CCC")
    private val favoriteRoute3 = FavoriteRoute(3, "CCC", "AAA")


    private suspend fun addOneFavoriteRoute() {
        flightSearchDao.insertFavoriteRoute(favoriteRoute0)
    }
    private suspend fun addFavoriteRoutes() {
        flightSearchDao.insertFavoriteRoute(favoriteRoute1)
        flightSearchDao.insertFavoriteRoute(favoriteRoute2)
        flightSearchDao.insertFavoriteRoute(favoriteRoute3)
    }
    private suspend fun deleteFavoriteRoute1() {
        flightSearchDao.deleteFavoriteRoute( "AAA", "BBB")
        }

    @Before
    fun createDb() = runBlocking {

        try {
            val context: Context = ApplicationProvider.getApplicationContext()
            appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
            flightSearchDao = appDatabase.flightSearchDao()
            Log.d("FlightSearchDaoTest", "Database created successfully")
        } catch (e: Exception) {
            fail("Failed to create database: ${e.message}")
        }
        val testAirports = listOf(airportA, airportB, airportC)
        testAirports.forEach { flightSearchDao.insertAirport(it) }
    }
    @After
    @Throws(IOException::class)
    fun closeDb(){
        if (::appDatabase.isInitialized) {
            appDatabase.close()
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertFavoriteRoute_shouldInsertFavoriteRoute() = runBlocking {
        addOneFavoriteRoute()
        val favoriteRoutes = flightSearchDao.getFavoriteRoutes().first()
            assertEquals(favoriteRoutes[0], favoriteRoute0)

    }

    @Test
    @Throws(Exception::class)
    fun getFavoriteRoutesByiataCode_shouldReturnMatchingFavoriteRoutes() = runBlocking {
        addFavoriteRoutes()
        val favoriteRoutes = flightSearchDao.getFavoriteRouteByIataCodes("CCC", "AAA").first()
        assertEquals(favoriteRoute3, favoriteRoutes)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavoriteRoute_shouldDeleteFavoriteRouteUsingIataCodes() = runBlocking {
        addFavoriteRoutes()
        deleteFavoriteRoute1()
        val favoriteRoutes = flightSearchDao.getFavoriteRoutes().first()
        assertEquals(favoriteRoutes.size, 2)
    }

    @Test
    @Throws(Exception::class)
    fun getFavoriteRoutes_shouldReturnAllSavedFavoriteRoutes() = runBlocking {
        addFavoriteRoutes()
        val favoriteRoutes = flightSearchDao.getFavoriteRoutes().first()
        assertEquals(favoriteRoutes.size, 3)
    }

    @Test
    fun getAllAirports_shouldReturnPrepoulatedData() = runBlocking {
        val airports = flightSearchDao.getAllAirports().first()
        assertTrue(airports.size == 3)
    }

    @Test
    @Throws(Exception::class)
    fun getAirportByIataCode_shouldReturnExactMatch() = runBlocking {
        val results = flightSearchDao.searchAirport("AAA").first()
        assertEquals(1, results.size)
        assertEquals("Airport A", results[0].airportName)
    }
    @Test
    @Throws(Exception::class)
    fun searchAirportByName_shouldReturnMatchingAirports() = runBlocking {
        val results = flightSearchDao.searchAirport("Airport").first()
        assertEquals(3, results.size)
        assertTrue(results.contains(airportA))
        assertTrue(results.contains(airportB))
        assertTrue(results.contains(airportC))
    }

    //borderline cases
    @Test
    fun searchAirport_withEmptyQuery_shouldReturnAllAirports() = runBlocking {
        val results = flightSearchDao.searchAirport("").first()
        assertEquals(3, results.size)
    }

    @Test
    fun getAirportByIataCode_withNonExistingCode_shouldReturnEmptyAirport() = runBlocking {
        val airport = flightSearchDao.getAirportByIataCode("ZZZ").first()
        assertNull(airport)
    }

    @Test
    fun getFavoriteRouteByCodes_ShouldReturnMatchingFavoriteRoute() = runBlocking {
        addFavoriteRoutes()
        val favoriteRoute = flightSearchDao.getFavoriteRouteByCodes("CCC", "AAA").first()
        assertEquals(favoriteRoute3, favoriteRoute)

    }



}