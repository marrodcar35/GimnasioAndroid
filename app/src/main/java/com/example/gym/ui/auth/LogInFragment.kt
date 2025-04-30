package com.example.gym.ui.auth


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gym.MainActivity
import com.example.gym.R
import com.example.gym.UserViewModel
import com.example.gym.databinding.FragmentLogInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LogInFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // ⚡ Muy importante para evitar memory leaks
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        // Aquí configurás botones, listeners, etc.
        setup()
    }

    private fun setup() {

        binding.button.setOnClickListener{
            val email = binding.editTextText2.text.toString().trim()
            val contra = binding.editTextTextPassword.text.toString().trim()
            if( email.isNotEmpty() &&
                contra.isNotEmpty())
                auth.signInWithEmailAndPassword(email, contra)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid
                            val db = FirebaseFirestore.getInstance()

                            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                                if (document != null) {
                                    val userName = document.getString("userName")
                                    val userEmail = FirebaseAuth.getInstance().currentUser?.email

                                    userViewModel.userName.value = userName
                                    userViewModel.email.value = userEmail

                                    // ⚠️ Ahora que ya tenés los datos, navega al Home
                                    showHome(userEmail ?: "")
                                } else {
                                    showAlert()
                                }
                            }
                        } else{
                            //para ver en el log cat los errores
                            val errorMessage = it.exception?.message ?: "Error de credenciales"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            Log.e("FIREBASE_AUTH", "Error al crear usuario", it.exception)
                            showAlert()
                        }
                    }else if(email.isEmpty()){
                binding.editTextText2.error = "El campo de correos no puede estar vacío"
            }else {
                binding.editTextTextPassword.error = "El campo de contraseña no puede estar vacío"
            }
        }
        binding.button3.setOnClickListener{
            findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
        }
    }
    private fun showHome(email:String){
        //Arrancar la pantalla de inicio
        val homeIntent = Intent(context,MainActivity::class.java).apply {
            putExtra("email",email )
            //putExtra("userName", userName)
        }
        startActivity(homeIntent)
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}