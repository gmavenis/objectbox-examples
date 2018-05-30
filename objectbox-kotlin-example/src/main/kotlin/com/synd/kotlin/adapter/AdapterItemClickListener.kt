package com.synd.kotlin.adapter

import com.synd.kotlin.model.AndroidVersion

interface AdapterItemClickListener {
    fun onItemClick(androidVersion: AndroidVersion)
}