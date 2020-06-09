package com.example.tp2.api

import retrofit2.http.GET
import retrofit2.http.Headers

interface ServiceApi {

    @GET("lists?hash=3f42b18b7f71498b166d1662848a5bec")
    suspend fun getUsers()

}