package io.github.kn65op.domag.data.model

import io.github.kn65op.domag.utils.types.HasUid

data class Depot(
    override val uid: Int? = null,

    val name: String,
    val parentId: Int?,
    val children: List<RawDepot>,
    val items: List<RawItem>,
) : HasUid