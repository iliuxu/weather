package com.weather

import android.app.Application
import android.content.Context

class WeatherApplication : Application() {
    companion object {
        const val TOKEN = "TAkhjf8d1nlSlspN" //彩云天气申请的令牌值
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}