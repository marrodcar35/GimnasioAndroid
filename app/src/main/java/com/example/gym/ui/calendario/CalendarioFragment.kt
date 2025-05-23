package com.example.gym.ui.calendario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gym.R
import com.example.gym.viewmodel.RutinaViewModel
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class CalendarioFragment : Fragment() {
    private val rutinaViewModel: RutinaViewModel by activityViewModels()

    private var fechaSeleccionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val calendarView = view.findViewById<MaterialCalendarView>(R.id.calendarView)
        val fechaTexto = view.findViewById<TextView>(R.id.fechaTexto)
        val rutinabtn = view.findViewById<Button>(R.id.rutinabtn)

        calendarView.post { calendarView.clearSelection() }
        fechaTexto.text = "SELECCIONA UNA FECHA"

        calendarView.setOnDateChangedListener { _, date, _ ->
            val dia = date.day
            val mes = date.month
            val año = date.year
            fechaSeleccionada = "$dia-$mes-$año"

            //fechaTexto.text = "Hoy es $fechaSeleccionada"

            rutinabtn.visibility = View.VISIBLE
            fechaTexto.visibility = View.GONE

            rutinabtn.text = "Ver rutina del $fechaSeleccionada"
        }

        rutinabtn.setOnClickListener {
            // Aquí podrías lanzar otro fragment, activity, o mostrar más info
            // Ejemplo simple:
            //fechaTexto.text = "Rutina de $fechaStr:\n- Pecho\n- Bíceps\n- Cardio 20min"

            rutinaViewModel.descripcion = ""
            rutinaViewModel.fecha = ""
            rutinaViewModel.ejercicios.clear()

            val bundle = Bundle()
            bundle.putString("fecha", fechaSeleccionada)
            findNavController().navigate(R.id.action_calendarioFragment_to_detalleRutinaFragment, bundle)
        }
    }
}