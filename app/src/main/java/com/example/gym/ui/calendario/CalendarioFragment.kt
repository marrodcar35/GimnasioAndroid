package com.example.gym.ui.calendario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gym.R
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class CalendarioFragment : Fragment() {
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

        calendarView.setOnDateChangedListener { _, date, _ ->
            val dia = date.day
            val mes = date.month + 1
            val año = date.year
            fechaTexto.text = "Hoy es $dia/$mes/$año"
        }
    }
}