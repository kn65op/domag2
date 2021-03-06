package io.github.kn65op.domag.data.model

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.repository.DatabaseRepository
import io.github.kn65op.domag.utils.types.HasUid

data class Category(
    override val uid: Int?,

    val name: String,
    val unit: String,
    val minimumDesiredAmount: FixedPointNumber?,
    val parentId: Int?,
    val children: List<RawCategory>,
    val items: List<RawItem>,
) : HasUid
