package com.synd.kotlin.db

interface OnDataChangedListener<T> {
    fun onChanged(t: List<T>?)
}