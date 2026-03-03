package com.inesengel.travelapp.infra.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inesengel.travelapp.infra.local.dao.TravelDestinationDao
import com.inesengel.travelapp.infra.local.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import project.model.TravelDestination
import project.model.TravelType
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TravelDestinationDaoTest {

    private lateinit var destinationDao: TravelDestinationDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        destinationDao = db.travelDestinationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadDestination() = runBlocking {
        val destination = TravelDestination(
            name = "Paris",
            type = TravelType.CITY_BREAK,
            price = 1200.0,
            rating = 5,
            country = "France",
            visited = false,
            duration = 4,
            imageResId = 123
        )

        val id = destinationDao.insertDestination(destination)

        val result = destinationDao.getDestinationsWithDetails().first()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Paris", result[0].destination.name)
        assertEquals(TravelType.CITY_BREAK, result[0].destination.type)
        assertEquals(1200.0, result[0].destination.price, 0.0)
    }

    @Test
    fun updateFavoriteStatus_updatesCorrectly() = runBlocking {
        val destination = TravelDestination(
            name = "Tokyo",
            type = TravelType.CITY_BREAK,
            price = 2000.0,
            rating = 5,
            country = "Japan",
            visited = true,
            isFavorite = false,
            duration = 10,
            imageResId = 456
        )
        val id = destinationDao.insertDestination(destination).toInt()

        destinationDao.updateFavoriteStatus(id, true)

        val favoriteList = destinationDao.getFavoriteDestinations().first()
        assertEquals(1, favoriteList.size)
        assertEquals(true, favoriteList[0].destination.isFavorite)
    }

    @Test
    fun deleteDestination_removesFromDb() = runBlocking {
        val destination = TravelDestination(
            name = "DeleteMe",
            type = TravelType.ADVENTURE,
            price = 0.0,
            rating = 1,
            country = "None",
            visited = false,
            duration = 1,
            imageResId = 0
        )
        val id = destinationDao.insertDestination(destination).toInt()

        destinationDao.deleteDestinationById(id)

        val result = destinationDao.getDestinationsWithDetails().first()
        assertEquals(0, result.size)
    }
}