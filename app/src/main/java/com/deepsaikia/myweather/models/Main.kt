package com.deepsaikia.myweather.models

import java.io.Serializable

data class Main(

    val sea_level:Float,
    val grnd_level:Float,
    val feels_like:Float,
    val temp: Float,
    val pressure: Float,
    val humidity: Float,
    val temp_min: Float,
    val temp_max: Float
) : Serializable