package com.synd.kotlin.db.entity

import com.synd.kotlin.model.ScoreModel
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ScoreEntity(
        @Id var id: Long = 0,
        var uid: Long? = null,
        var subject: String? = null,
        var score: Int? = null) {

    fun toModel(): ScoreModel {
        return ScoreModel(subject, score)
    }
}
