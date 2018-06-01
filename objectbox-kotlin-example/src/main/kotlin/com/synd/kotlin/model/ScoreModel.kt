package com.synd.kotlin.model

class ScoreModel {
    var id: Long?
    var subject: String?
    var score: Int?

    constructor(id: Long?, subject: String?, score: Int?) {
        this.id = id
        this.subject = subject
        this.score = score
    }

    override fun toString(): String {
        return StringBuilder()
                .append(subject).append(": ")
                .append(score).toString()
    }
}