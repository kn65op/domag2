package io.github.kn65op.domag.database.operations

import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.relations.CategoryWithContents

suspend fun AppDatabase.deleteCategory(category: CategoryWithContents) {
    val categoryDao = categoryDao()
    val itemDao = itemDao()
    category.categories.forEach {
        it.uid?.let { childId ->
            deleteCategory(categoryDao.findWithContentsByIdImmediately(childId))
        }
    }
    category.items.forEach {
        itemDao.delete(it)
    }
    categoryDao.delete(category.category)
}
