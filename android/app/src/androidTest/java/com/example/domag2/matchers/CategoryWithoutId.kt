package com.example.domag2.matchers

import com.example.domag2.database.entities.Category
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class IsCategoryEqualRegardlessId(val category: Category) : BaseMatcher<Category>() {
    override fun matches(actual: Any?): Boolean =
        if (actual is Category)
            category.name == actual.name
        else
            false

    override fun describeTo(description: Description?) {
        description?.appendText("Should be equal regardless of id")
    }

    override fun describeMismatch(item: Any?, mismatchDescription: Description?) {
        mismatchDescription?.appendText("${item as Category} his not equal to $category")
    }
}

fun isEqualRegardlessId(category: Category) = IsCategoryEqualRegardlessId(category)
