package com.example.jetpack.ui.listjetpack

import com.example.jetpack.data.JetpackModel
import io.reactivex.Single
import retrofit2.http.GET

interface JetpackApi {
    @GET("DevTides/DogsApi/master/dogs.json")
    fun getData(): Single<List<JetpackModel>>
}