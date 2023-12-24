package com.utad.ideasapp.ui

import android.app.Application
import androidx.room.Room
import com.utad.ideasapp.room.IdeasDataBase

class MyApplication : Application() {

    lateinit var dataBase: IdeasDataBase

    override fun onCreate() {
        super.onCreate()
        //Inicializamos nuestra base de datos con el databasebuilder
        dataBase = Room.databaseBuilder(
            this, //Indicamos el contexto
            IdeasDataBase::class.java,//Indicamo de qué clase es nuestra BD
            "IdeasDataBase" // así como su nombre
        )
            .build()//Construimos la base de datos
    }
}