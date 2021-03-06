package com.synd.kotlin.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Repository {
    companion object {
        private lateinit var retrofit: Retrofit
        private lateinit var builder: Retrofit.Builder
        private val httpClient = OkHttpClient.Builder()

        fun <S> createService(serviceClass: Class<S>, host: String): S {
            return createService(serviceClass, host, null)
        }

        fun <S> createService(serviceClass: Class<S>, host: String, authToken: Map<String, String>?): S {
            if (authToken != null) {
                var interceptor = AuthenticationInterceptor(authToken!!)
                if (interceptor !in httpClient.interceptors()) {
                    httpClient.addInterceptor(interceptor)
                    builder.client(httpClient.build())
                    retrofit = builder.build()
                }
            }
            builder = Retrofit.Builder().baseUrl(host)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            retrofit = builder.build()
            return retrofit!!.create(serviceClass)
        }

    }

    // Request headers
    class AuthenticationInterceptor(private val authToken: Map<String, String>) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val builder = original.newBuilder()
            for (key in authToken.keys) {
                builder.header(key, authToken.getValue(key))
            }
            val request = builder.build()
            return chain.proceed(request)
        }
    }

}