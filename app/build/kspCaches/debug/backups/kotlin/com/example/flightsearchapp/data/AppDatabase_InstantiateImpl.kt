package com.example.flightsearchapp.`data`

import kotlin.reflect.KClass

internal fun KClass<AppDatabase>.instantiateImpl(): AppDatabase =
    com.example.flightsearchapp.`data`.AppDatabase_Impl()
