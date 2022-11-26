package com.deepsaikia.myweather.models

import java.io.Serializable

data class Main(

    val feels_like:Float,
    val temp: Float,
    val pressure: Float,
    val humidity: Float
) : Serializable