package com.synd.kotlin.db.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ScoreEntity(
        @Id var id: Long = 0,
        var uid: Long? = null,
        var subject: String? = null,
        var score: Int? = null
)
