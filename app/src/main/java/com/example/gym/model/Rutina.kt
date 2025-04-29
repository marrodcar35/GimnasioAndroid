package com.example.gym.model

data class Rutina (
    //val titulo: String = "",
    var descripcion: String = "",
    var fecha: String = "",
    var ejercicios: List<Ejercicio> = emptyList()
)