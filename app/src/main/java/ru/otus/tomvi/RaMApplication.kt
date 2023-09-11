package ru.otus.tomvi

import android.app.Application

class RaMApplication : Application() {

    private lateinit var _serviceLocator: ServiceLocator

    val serviceLocator: ServiceLocator
        get() = _serviceLocator

    override fun onCreate() {
        super.onCreate()

        _serviceLocator = ServiceLocator(this)
    }
}
