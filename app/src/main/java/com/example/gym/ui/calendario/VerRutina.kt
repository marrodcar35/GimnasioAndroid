package com.example.gym.ui.calendario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gym.R
import com.example.gym.model.Rutina
import com.google.firebase.database.FirebaseDatabase

class VerRutina: Fragment() {
    private lateinit var titulo: TextView
    private lateinit var descripcion: EditText
    private lateinit var lista: TextView
    private lateinit var guardar: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_rutina, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fecha = arguments?.getString("fecha")
        titulo = view.findViewById<TextView>(R.id.fechaRut)
        descripcion = view.findViewById<EditText>(R.id.editDescripcionRut)
        lista = view.findViewById<TextView>(R.id.listaRut)
        guardar = view.findViewById<Button>(R.id.guardaRut)

        titulo.text = "Rutina de $fecha"

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("rutinas").child("$fecha") // ejemplo de fecha

        myRef.get().addOnSuccessListener { snapshot ->
            Log.d("Firebase", "Snapshot completo: ${snapshot.value}")

            if (snapshot.exists() && snapshot.hasChildren()) {
                val rutina = snapshot.getValue(Rutina::class.java)
                if (rutina != null) {
                    Log.d("Firebase", "Rutina encontrada: $rutina")
                    descripcion.setText(rutina.descripcion)
                    lista.text = "Probando"
                } else {
                    Log.d("Firebase", "Rutina nula (estructura incorrecta)")
                }
            } else {
                Log.d("Firebase", "No hay rutina en esta fecha (snapshot vacío o sin hijos)")
            }
        }

        guardar.setOnClickListener {
            myRef.get().addOnSuccessListener { snapshot ->
                Log.d("Firebase", "Snapshot en guardar: ${snapshot.value}")

                if (snapshot.exists() && snapshot.hasChildren()) {
                    val rutina = snapshot.getValue(Rutina::class.java)
                    if (rutina != null) {
                        Log.d("Firebase", "Rutina encontrada para actualizar: $rutina")

                        myRef.child("descripcion").setValue(descripcion.text.toString())
                            .addOnSuccessListener {
                                Log.d("Firebase", "Descripción actualizada correctamente")
                            }
                            .addOnFailureListener { error ->
                                Log.e("Firebase", "Error al actualizar descripción: ${error.message}")
                            }

                    } else {
                        Log.d("Firebase", "Rutina nula al intentar actualizar")
                    }
                } else {
                    Log.d("Firebase", "No existe rutina, creando nueva")

                    val nuevaRutina = Rutina(
                        descripcion = descripcion.text.toString(),
                        fecha = fecha ?: "",
                        ejercicios = listOf()
                    )

                    myRef.setValue(nuevaRutina)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Nueva rutina guardada correctamente")
                        }
                        .addOnFailureListener { error ->
                            Log.e("Firebase", "Error al guardar nueva rutina: ${error.message}")
                        }
                }
            }
        }


    }


}