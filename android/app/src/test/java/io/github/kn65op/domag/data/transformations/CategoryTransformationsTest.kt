package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.database.entities.CategoryLimit
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.model.RawCategory
import io.github.kn65op.domag.data.model.Category as ModelCategory
import io.github.kn65op.domag.data.database.entities.Category as DbCategory
import org.junit.Test

class CategoryTransformationsTest {
    private val uid = 1
    private val name = "Name"
    private val unit = "Unit"
    private val dbCategoryBase = DbCategory(uid = uid, parentId = null, name = name, unit = unit)
    private val dbCategoryWithContentsBase = CategoryWithContents(category = dbCategoryBase)
    private val modelCategoryBase = ModelCategory(
        uid = uid,
        name = name,
        unit = unit,
        minimumDesiredAmount = null,
        children = emptyList(),
        items = emptyList(),
        parentId = null,
    )
    private val modelRawCategoryBase = RawCategory(
        uid = uid,
        name = name,
        unit = unit,
        parentId = null,
    )
    private val minimumAmount = FixedPointNumber(2.32)
    private val parentId = 2323

    @Test
    fun `should transform category without parent, children and limit`() {

        assertThat(dbCategoryWithContentsBase.toModelCategory(), equalTo(modelCategoryBase))
    }

    @Test
    fun `should transform category to raw category`() {
        assertThat(dbCategoryBase.toModelRawCategory(), equalTo(modelRawCategoryBase))
    }

    @Test
    fun `should transform category without uid`() {
        val db = dbCategoryBase.copy(uid = null)
        val dbCategoryWithContents = CategoryWithContents(category = db)
        val modelCategory = modelCategoryBase.copy(uid = null)

        assertThat(dbCategoryWithContents.toModelCategory(), equalTo(modelCategory))
    }

    @Test
    fun `should transform category with limit`() {
        val dbCategoryWithContents = dbCategoryWithContentsBase.copy(
            limits = CategoryLimit(
                uid = null,
                categoryId = uid,
                minimumDesiredAmount = minimumAmount,
            )
        )
        val modelCategory = modelCategoryBase.copy(minimumDesiredAmount = minimumAmount)

        assertThat(dbCategoryWithContents.toModelCategory(), equalTo(modelCategory))
    }

    @Test
    fun `should transform children to raw categories`() {
        val childCategory1Id = 22
        val childCategory1Name = "Child 1"
        val childCategory1Unit = "Child Unit 1"
        val childCategory1 = DbCategory(
            uid = childCategory1Id,
            name = childCategory1Name,
            parentId = uid,
            unit = childCategory1Unit,
        )
        val childCategory2Id = 222
        val childCategory2Name = "Child 2"
        val childCategory2Unit = "Child Unit 2"
        val childCategory2 = DbCategory(
            uid = childCategory2Id,
            name = childCategory2Name,
            parentId = uid,
            unit = childCategory2Unit,
        )

        val dbCategory =
            dbCategoryWithContentsBase.copy(categories = listOf(childCategory1, childCategory2))

        val childModelCategory1 = RawCategory(
            uid = childCategory1Id,
            name = childCategory1Name,
            unit = childCategory1Unit,
            parentId = uid,
        )

        val childModelCategory2 = RawCategory(
            uid = childCategory2Id,
            name = childCategory2Name,
            unit = childCategory2Unit,
            parentId = uid,
        )

        val modelCategory =
            modelCategoryBase.copy(children = listOf(childModelCategory1, childModelCategory2))

        assertThat(dbCategory.toModelCategory(), equalTo(modelCategory))
    }

    @Test
    fun `should transform parent id`() {
        val dbCategory = dbCategoryBase.copy(parentId = parentId)
        val dbCategoryWithContents = dbCategoryWithContentsBase.copy(category = dbCategory)

        val modelCategory = modelCategoryBase.copy(parentId = parentId)

        assertThat(dbCategoryWithContents.toModelCategory(), equalTo(modelCategory))
    }

    @Test
    fun `should transform parent id in RawCategory`() {
        val dbCategory = dbCategoryBase.copy(parentId = parentId)

        val rawModelCategory = modelRawCategoryBase.copy(parentId = parentId)

        assertThat(dbCategory.toModelRawCategory(), equalTo(rawModelCategory))
    }
}