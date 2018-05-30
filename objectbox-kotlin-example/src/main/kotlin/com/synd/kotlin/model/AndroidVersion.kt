package com.synd.kotlin.model

data class AndroidVersion(val name: String,
                          val ver: String,
                          val api: String) {
    override fun toString(): String {
        return StringBuilder()
                .append(name).append(", ")
                .append(ver).append(", ")
                .append(api).toString()
    }
}