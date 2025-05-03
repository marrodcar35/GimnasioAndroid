package com.example.gym.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gym.model.Ejercicio

class RutinaViewModel : ViewModel() {
    var descripcion: String = ""
    var fecha: String = ""
    var ejercicios: MutableList<Ejercicio> = mutableListOf()
}