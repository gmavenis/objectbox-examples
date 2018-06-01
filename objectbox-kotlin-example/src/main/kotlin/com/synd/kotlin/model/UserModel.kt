package com.synd.kotlin.model

import com.google.gson.annotations.SerializedName

class UserModel {
    var id: Long?
    var uid: Long?
    var name: String?
    var age: Int?
    @SerializedName("scores")
    var scores: List<ScoreModel>?

    constructor(id: Long?, uid: Long?, name: String?, age: Int?, scores: List<ScoreModel>?) {
        this.id = id
        this.uid = uid
        this.name = name
        this.age = age
        this.scores = scores
    }

    fun scoresToString(): String {
        val builder = StringBuilder();
        scores?.forEach {
            builder.append(it.toString()).append("\n")
        }
        return builder.toString()
    }

    override fun toString(): String {
        return StringBuilder()
                .append(uid).append(", ")
                .append(name).append(", ")
                .append(age).toString()
    }
}