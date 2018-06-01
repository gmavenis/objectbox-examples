package com.synd.kotlin.model

class ScoreModel {
    var subject: String?
    var score: Int?

    constructor(subject: String?, score: Int?) {
        this.subject = subject
        this.score = score
    }

    override fun toString(): String {
        return StringBuilder()
                .append(subject).append(": ")
                .append(score).toString()
    }
}