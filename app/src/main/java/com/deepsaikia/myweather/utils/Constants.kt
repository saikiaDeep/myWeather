package com.deepsaikia.myweather.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Constants {

    const val APP_ID: String = "c18ae9ce56f85cd9ac24fdc5f70b6211"
    const val BASE_URL: String = "http://api.openweathermap.org/data/"
    const val METRIC_UNIT: String = "metric"
    const val PREFERENCE_NAME = "MyWeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetWork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}