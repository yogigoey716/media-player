package com.yogigoey.mediaplayer.modules

import com.google.gson.GsonBuilder
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module{
    single{GsonBuilder().create()}
    single<Retrofit>{
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
}