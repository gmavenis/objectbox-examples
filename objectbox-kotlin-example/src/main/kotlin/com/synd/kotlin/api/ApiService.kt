package com.synd.kotlin.api

import com.synd.kotlin.model.AndroidVersion
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("android/jsonarray/")
    fun getAndroidVersion(): Observable<List<AndroidVersion>>

}