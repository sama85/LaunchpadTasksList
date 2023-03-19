package com.example.launchpadtaskslist.network

import TaskContainer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


interface TasksApiService {
    @Headers(
        "Authorization: $TOKEN",
        "Accept-Language: ar"
    )
    /** relative_date__lte = 0 -> retrieve all old tasks up to todat
     * relative date of old dates -> -ve (< 0)
     * relative date of today -> 0
     * relative date of future dates -> +ve (> 0)
     */
    @GET(TASKS_ENDPOINT + "?relative_date__lte=0")
    suspend fun getTasks(): TaskContainer

}

object Network {

    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
        .build()

    private val retrofit =
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).client(
            client)
            .baseUrl(BASE_URL).build()

    val tasksApiService: TasksApiService by lazy {
        retrofit.create(TasksApiService::class.java)
    }

}