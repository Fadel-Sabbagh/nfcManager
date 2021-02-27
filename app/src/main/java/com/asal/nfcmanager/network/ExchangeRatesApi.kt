package com.asal.nfcmanager.network

import com.asal.nfcmanager.helpers.Constants
import com.asal.nfcmanager.network.models.ExchangeRates

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ExchangeRatesApi {

    @GET("latest")
    fun GetRates(): Call<ExchangeRates>

    companion object {
        val instance: ExchangeRatesApi by lazy {
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            retrofit.create(ExchangeRatesApi::class.java)
        }
    }
}