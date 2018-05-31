package com.synd.kotlin.db.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class UserEntity(
        @Id var id: Long = 0,
        var uid: Long? = null,
        var name: String? = null,
        var age: Int? = null
)
