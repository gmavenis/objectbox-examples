package com.synd.kotlin.api

import com.synd.kotlin.model.AndroidVersion
import com.synd.kotlin.model.UserModel
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    @GET("android/jsonarray/")
    fun getAndroidVersion(): Observable<List<AndroidVersion>>

    @GET("user_list")
    fun getUserList(): Observable<List<UserModel>>

}