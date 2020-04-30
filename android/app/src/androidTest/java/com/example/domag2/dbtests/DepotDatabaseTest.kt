package com.example.domag2.dbtests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domag2.database.daos.DepotDao
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.dbtests.common.getFromLiveData
import com.example.domag2.dbtests.data.*
import com.example.domag2.matchers.isEqualRegardlessId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
open class DepotDatabaseTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var depotDao: DepotDao
    private lateinit var db: AppDatabase
    val matchDepotStartingWithName =
        arrayContainingInAnyOrder(
            isEqualRegardlessId(mainDepot1.depot),
            isEqualRegardlessId(mainDepot2.depot)
        )

    @Before
    fun createDb() {
        db = com.example.domag2.dbtests.data.createDb(ApplicationProvider.getApplicationContext())
        depotDao = db.depotDao()
    }

    @Before
    fun fillDatabase() {
        fillData(db)
    }

    @After
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun findDepotsByExactName() {
        val byName = getFromLiveData(depotDao.findByName(depot2InMainDepot1Name))
        assertThat(
            byName.toTypedArray(),
            arrayContainingInAnyOrder(isEqualRegardlessId(depot2InMainDepot1.depot))
        )
    }

    @Test
    fun findDepotsStartsWith() {
        val byName = getFromLiveData(depotDao.findByName("name*"))
        assertThat(byName.size, equalTo(2))
        assertThat(
            byName.toTypedArray(),
            matchDepotStartingWithName
        )
    }

    @Test
    fun getAll() {
        val all = getFromLiveData(depotDao.getAll())
        assertThat(
            all.toTypedArray(),
            arrayContainingInAnyOrder(
                isEqualRegardlessId(mainDepot1.depot),
                isEqualRegardlessId(mainDepot2.depot),
                isEqualRegardlessId(depot1InMainDepot1.depot),
                isEqualRegardlessId(depot2InMainDepot1.depot)
            )
        )
    }

    @Test
    fun findById() {
        val toFind = getFromLiveData(depotDao.findByName(mainDepot1.depot.name))
        assertThat(toFind.size, equalTo(1))

        val found = getFromLiveData(depotDao.findWithContentsById(toFind[0].uid as Int))

        assertThat(toFind[0], equalTo(found.depot))
    }

    @Test
    fun findByIds() {
        val toFind = getFromLiveData(depotDao.findByName("name*"))

        assertThat(toFind.size, greaterThan(1))

        val found =
            getFromLiveData(depotDao.findWithContentsById(toFind.map { it.uid as Int }
                .toTypedArray()))

        assertThat(
            found.map { it.depot }.toTypedArray(),
            matchDepotStartingWithName
        )
    }

    @Test
    fun delete() = runBlocking {
        val toRemove = getFromLiveData(depotDao.findByName(mainDepot1.depot.name))
        assertThat(toRemove.size, equalTo(1))

        depotDao.deleteOnly(toRemove[0])

        val all = getFromLiveData(depotDao.getAll())

        assertThat(all, not(hasItem(toRemove[0])))
    }

    @Test
    fun deleteShouldDeleteAlsoAllAllDepotsInside() = runBlocking {
        val shouldBeRemoved = getFromLiveData(depotDao.findByName(mainDepot1Name)).plus(
            getFromLiveData(depotDao.findByName(depot1InMainDepot1Name))
        ).plus(getFromLiveData(depotDao.findByName(depot1InMainDepot1Name)))
        assertThat(shouldBeRemoved.size, greaterThan(1))

        depotDao.deleteWithChildren(shouldBeRemoved[0])

        val all = getFromLiveData(depotDao.getAll())

        assertThat(all, not(hasItem(shouldBeRemoved[0])))
        assertThat(all, not(hasItem(shouldBeRemoved[1])))
        assertThat(all, not(hasItem(shouldBeRemoved[2])))
    }

    @Test
    fun depotWithItems() {
        val depotWithItems = getFromLiveData(depotDao.findWithContentsById(3))

        assertThat(
            depotWithItems.items.toTypedArray(),
            arrayContainingInAnyOrder(item2, item5, item6, item7)
        )
    }

    @Test
    fun depotWithChildren() {
        val depotWithChildren = getFromLiveData(depotDao.findWithContentsById(1))

        assertThat(
            depotWithChildren.depots.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(depot1InMainDepot1.depot), isEqualRegardlessId(
                    depot2InMainDepot1.depot
                )
            )
        )
    }

    @Test
    fun rootDepots() {
        val rootDepots = getFromLiveData(depotDao.findRootDepots())

        assertThat(
            rootDepots.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(mainDepot1.depot), isEqualRegardlessId(mainDepot2.depot)
            )
        )
    }
}