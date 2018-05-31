package com.synd.kotlin.viewmodel

import android.arch.lifecycle.ViewModel
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.query.Query

class BaseViewModel<T> : ViewModel() {

    private lateinit var liveData: ObjectBoxLiveData<T>

    fun getLiveData(query: Query<T>): ObjectBoxLiveData<T> {
        liveData = ObjectBoxLiveData<T>(query)
        return liveData
    }
}