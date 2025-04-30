package com.example.gym.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gym.R
import com.example.gym.UserViewModel
import com.example.gym.databinding.FragmentLogInBinding
import com.example.gym.databinding.FragmentProfileBinding
import com.example.gym.ui.auth.LogInFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class ProfileFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // ⚡ Muy importante para evitar memory leaks
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val name = doc.getString("userName")
                val email = doc.getString("email")
                val peso = doc.getString("peso")
                val altura = doc.getString("altura")
                binding.textView3.text = email ?: "Sin nombre"
                binding.textView6.text = name ?: "Sin correo"
                binding.textView8.setText(peso)
                binding.textView9.setText(altura)
            }
        }

        binding.button.setOnClickListener {
            enableEditing(true)
        }

        binding.button2.setOnClickListener {
            saveProfileChanges()
        }

        binding.button3.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            requireActivity().recreate() // Fuerza a reiniciar MainActivity, que evaluará sesión
        }
    }
    private fun enableEditing(enabled: Boolean) {
        binding.textView8.isEnabled = enabled
        binding.textView9.isEnabled = enabled
    }
    private fun saveProfileChanges() {
        val newPeso = binding.textView8.text.toString().trim()
        val newAltura = binding.textView9.text.toString().trim()

        userViewModel.peso.value = newPeso
        userViewModel.altura.value = newAltura

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .update("peso", newPeso, "altura", newAltura)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                enableEditing(false)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show()
            }
    }


}