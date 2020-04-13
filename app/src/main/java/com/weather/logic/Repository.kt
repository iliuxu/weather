package com.weather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.weather.logic.dao.PlaceDao
import com.weather.logic.model.Place
import com.weather.logic.model.Weather
import com.weather.logic.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

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
    fun refreshWeather(lng: String, lat: String, placeName: String) = liveData(Dispatchers.IO) {
        val  result = try {
            coroutineScope {
                val deferredRealtime = async {
                    WeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    WeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather = Weather(realtimeResponse.result.realtime,
                        dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}" +
                                    "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
        emit(result as Result<Weather>)
    }
    private fun <T> fire(context: CoroutineContext, block: suspend() -> Result<T>)  =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavePlace() = PlaceDao.getSavePlace()
    fun isSavedPlace() = PlaceDao.isPlaceSaved()
}