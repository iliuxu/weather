package com.weather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.weather.WeatherApplication
import com.weather.logic.model.Place

object PlaceDao {
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavePlace() : Place {
        val placeJson = sharedPreferences().getString("place", "")
        return  Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun  sharedPreferences() = WeatherApplication.context.
    getSharedPreferences("weather", Context.MODE_PRIVATE)
}