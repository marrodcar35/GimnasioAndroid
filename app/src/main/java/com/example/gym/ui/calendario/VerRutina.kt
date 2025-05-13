package com.example.gym.ui.calendario

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gym.R
import com.example.gym.model.Ejercicio
import com.example.gym.model.Rutina
import com.example.gym.viewmodel.RutinaViewModel
import com.google.firebase.firestore.FirebaseFirestore

class VerRutina : Fragment() {
    private val rutinaViewModel: RutinaViewModel by activityViewModels()

    private lateinit var titulo: TextView
    private lateinit var descripcion: EditText
    //private lateinit var lista: TextView
    private lateinit var guardar: Button
    private lateinit var botonNewEjer: Button
    //private var ejercicios: List<Ejercicio> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_rutina, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var fecha = arguments?.getString("fecha")
        titulo = view.findViewById(R.id.fechaRut)
        descripcion = view.findViewById(R.id.editDescripcionRut)
        //lista = view.findViewById(R.id.listaRut)
        guardar = view.findViewById(R.id.guardaRut)
        botonNewEjer = view.findViewById(R.id.nuevoejerbutton)

        descripcion.setText("")

        val db = FirebaseFirestore.getInstance()
        val rutinaRef = db.collection("rutinas").document(fecha ?: rutinaViewModel.fecha)

        if (rutinaViewModel.fecha.isEmpty() && fecha != null) {
            titulo.text = "Rutina del $fecha"
            rutinaViewModel.fecha = fecha

            // Leer la rutina desde Firestore
            rutinaRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val rutina = document.toObject(Rutina::class.java)
                        if (rutina != null) {
                            Log.d("Firestore", "Rutina encontrada: $rutina")

                            descripcion.setText(rutina.descripcion)
                            rutinaViewModel.descripcion=rutina.descripcion

                            //lista.text = imprimeEjercicios(rutina.ejercicios)
                            rutinaViewModel.ejercicios=rutina.ejercicios.toMutableList()
                            mostrarEjercicios(view)
                        } else {
                            Log.d("Firestore", "Documento inválido para Rutina")
                        }
                    } else {
                        cambiaTamañoScroll(view)
                        Log.d("Firestore", "No hay rutina para esta fecha")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al leer rutina: ${e.message}")
                }
        } else {
            titulo.text = "Rutina de ${rutinaViewModel.fecha}"

            descripcion.setText(rutinaViewModel.descripcion)

            //lista.text = imprimeEjercicios(rutinaViewModel.ejercicios)
            mostrarEjercicios(view)
        }

        botonNewEjer.setOnClickListener{
            rutinaViewModel.descripcion = descripcion.text.toString()
            findNavController().navigate(R.id.action_detalleRutinaFragment_to_ejercicioFragment)
        }

        // Guardar/Actualizar rutina al pulsar botón
        guardar.setOnClickListener {
            val nuevaRutina = Rutina(
                descripcion = descripcion.text.toString(),
                fecha = fecha ?: rutinaViewModel.fecha,
                ejercicios = rutinaViewModel.ejercicios
            )

            rutinaViewModel.descripcion = nuevaRutina.descripcion
            rutinaViewModel.fecha = nuevaRutina.fecha
            rutinaViewModel.ejercicios = nuevaRutina.ejercicios.toMutableList()

            rutinaRef.set(nuevaRutina)
                .addOnSuccessListener {
                    Toast.makeText(context, "Rutina guardada", Toast.LENGTH_SHORT).show()
                    Log.d("Firestore", "Rutina guardada correctamente")
                }
                .addOnFailureListener { error ->
                    Log.e("Firestore", "Error al guardar rutina: ${error.message}")
                }
        }
    }

    private fun mostrarEjercicios(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.containerList)
        container?.removeAllViews()

        cambiaTamañoScroll(view)

        rutinaViewModel.ejercicios.forEachIndexed { index, ejercicio ->
            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val texto = TextView(requireContext()).apply {
                text = "NOMBRE: ${ejercicio.nombre}\n" +
                        "DESCRIPCION: ${ejercicio.descripcion}\n" +
                        "REPETICIONES: ${ejercicio.repeticiones}\n" +
                        "SERIES: ${ejercicio.series}\n\n"
                textSize = 14f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val sizeInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50f, // tamaño cuadrado en dp
                resources.displayMetrics
            ).toInt()

            val botonEliminar = Button(requireContext()).apply {
                text = "X"
                textSize = 14f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD

                layoutParams = LinearLayout.LayoutParams(sizeInDp, sizeInDp).apply {
                    topMargin = 75
                }

                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 30f
                    setColor(Color.RED)
                }

                setOnClickListener {
                    rutinaViewModel.ejercicios.removeAt(index)
                    mostrarEjercicios(view)
                }
            }

            fila.addView(texto)
            fila.addView(botonEliminar)
            container?.addView(fila)
        }
    }

    private fun cambiaTamañoScroll(view: View){
        val scroll = view.findViewById<NestedScrollView>(R.id.scrollEjercicios)
        val container = view.findViewById<LinearLayout>(R.id.containerList)

        if(rutinaViewModel.ejercicios.size==0) {
            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val texto = TextView(requireContext()).apply {
                text = "No hay ejercicios para este dia"
                textSize = 14f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            fila.addView(texto)
            container?.addView(fila)
        }

        val alturaDp = when (rutinaViewModel.ejercicios.size) {
            0 -> 50
            1 -> 137
            else -> 275
        }

        val alturaPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            alturaDp.toFloat(),
            resources.displayMetrics
        ).toInt()
        scroll.post {
            scroll.layoutParams.height = alturaPx
            scroll.requestLayout()
        }

    }

}