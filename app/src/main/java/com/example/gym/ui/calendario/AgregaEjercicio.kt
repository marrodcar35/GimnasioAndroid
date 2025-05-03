package com.example.gym.ui.calendario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gym.R
import com.example.gym.model.Ejercicio
import com.example.gym.viewmodel.RutinaViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AgregaEjercicio : Fragment() {
    private val rutinaViewModel: RutinaViewModel by activityViewModels()

    private lateinit var nombre: EditText
    private lateinit var descripcion: EditText
    private lateinit var repeticiones: EditText
    private lateinit var series: EditText

    private lateinit var guardar: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_ejercicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nombre = view.findViewById(R.id.editNombre)
        descripcion = view.findViewById(R.id.editDescripcion)
        repeticiones = view.findViewById(R.id.editRepeticiones)
        series = view.findViewById(R.id.editSeries)
        guardar = view.findViewById(R.id.agregarbutton)

        nombre.setText("")
        descripcion.setText("")
        repeticiones.setText("")
        series.setText("")

        guardar.setOnClickListener {
            if(series.text.toString().toIntOrNull() != null && repeticiones.text.toString().toIntOrNull() != null) {
                val nuevoEjercicio = Ejercicio(
                    nombre = nombre.text.toString(),
                    descripcion = descripcion.text.toString(),
                    repeticiones = repeticiones.text.toString().toInt(),
                    series = series.text.toString().toInt()
                )

                rutinaViewModel.ejercicios.add(nuevoEjercicio)
                findNavController().navigate(R.id.action_ejercicioFragment_to_detalleRutinaFragment)
            } else {
                Toast.makeText(context, "Las repeticiones y ejercicios tienen que ser numeros", Toast.LENGTH_LONG).show()
            }
        }
    }
}