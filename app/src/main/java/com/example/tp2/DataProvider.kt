package com.example.tp2

import com.example.tp2.api.ServiceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataProvider {

    private const val BASE_URL = "http://tomnab.fr/todo-api"

    private val service = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServiceApi::class.java)

    suspend fun getUsersFromApi() = service.getUsers()

}