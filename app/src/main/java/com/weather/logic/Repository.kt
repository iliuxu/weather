package com.weather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.weather.logic.model.Place
import com.weather.logic.model.PlaceResponse
import com.weather.logic.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val  result = try {
            val placeResponse = WeatherNetwork.searchPlaces(query)
            Log.d("repository", placeResponse.status)
            if (placeResponse.status == "ok") {
                val  places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("Response status is ${placeResponse.status}"))
            }
        } catch (e : Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result as Result<*>)
    }
}