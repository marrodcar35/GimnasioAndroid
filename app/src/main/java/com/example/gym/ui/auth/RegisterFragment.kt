package com.example.gym.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gym.MainActivity
import com.example.gym.R
import com.example.gym.model.UserViewModel
import com.example.gym.databinding.FragmentRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // evitar memory leaks ;)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        // Aquí configurás botones, listeners, etc.
        setup()
    }
    private fun setup() {
        binding.button.setOnClickListener{

            val db = FirebaseFirestore.getInstance()

            val email = binding.editTextText2.text.toString().trim()
            val contra = binding.editTextTextPassword.text.toString().trim()
            val userName =  binding.editTextText.text.toString().trim()

            //Comprobar la estrutura correcta
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextText2.error = "Correo electrónico inválido"
                return@setOnClickListener
            }

            if( email.isNotEmpty() &&
                contra.isNotEmpty()&&
                userName.isNotEmpty())
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser!!.uid
                            val db = FirebaseFirestore.getInstance()

                            val user = hashMapOf(
                                "email" to email,
                                "userName" to userName
                            )

                            db.collection("users").document(userId).set(user)
                                .addOnSuccessListener {
                                    userViewModel.email.value = email
                                    userViewModel.userName.value = userName

                                    showHome(email, userName)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            val errorMessage = task.exception?.message ?: "Error desconocido"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            Log.e("FIREBASE_AUTH", "Error al crear usuario", task.exception)
                            showAlert()
                        }
                    }
            else if(email.isEmpty()){
                binding.editTextText2.error = "El campo de correos no puede estar vacío"
            }else if(contra.isEmpty()){
                binding.editTextTextPassword.error = "El campo de contraseña no puede estar vacío"
            } else {
                binding.editTextText.error = "El campo de nombre de usuario no puede estar vacío"
            }
        }
        binding.button3.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
        }
    }
    //Mostrar Alerta de error al logearse
    private fun showAlert(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    //Conducir a la pantalla de inicio
    private fun showHome(email:String, userName:String){
        val homeIntent = Intent(context,MainActivity::class.java).apply {
            putExtra("email",email)
            putExtra("userName", userName)
        }
        startActivity(homeIntent)
    }


}