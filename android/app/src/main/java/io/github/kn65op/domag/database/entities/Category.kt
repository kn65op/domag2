package io.github.kn65op.domag.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import io.github.kn65op.domag.utils.HasParent
import io.github.kn65op.domag.utils.HasUid

@Fts4
@Entity
data class Category (
    @PrimaryKey @ColumnInfo(name = "rowid")
    override val uid: Int? = null,

    override val parentId: Int? = null,

    val name: String,

    val unit: String
) : HasParent, HasUid