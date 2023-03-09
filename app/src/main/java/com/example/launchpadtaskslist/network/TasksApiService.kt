package com.example.launchpadtaskslist.network

import TaskContainer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


interface TasksApiService {
    @Headers("Authorization: $TOKEN")
    @GET(TASKS_ENDPOINT)
    suspend fun getTasks() : TaskContainer

}

object Network{

    private val retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL).build()

    val tasksApiService : TasksApiService by lazy {
        retrofit.create(TasksApiService::class.java)
    }

}