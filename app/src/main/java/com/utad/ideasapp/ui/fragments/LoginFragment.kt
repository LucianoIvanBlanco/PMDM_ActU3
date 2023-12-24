package com.utad.ideasapp.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.utad.ideasapp.data_store.DataStoreManager
import com.utad.ideasapp.databinding.FragmentLoginBinding
import com.utad.ideasapp.ui.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private lateinit var _binding: FragmentLoginBinding
    private val binding: FragmentLoginBinding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Primero chequeamos si el ususario esta guardado en datastore
        lifecycleScope.launch(Dispatchers.IO) {
            checkIsUserLogged()
        }
        binding.btnRegister.setOnClickListener {
            goToRegister()
            clearTextFields()
            hideKeyBoard()
        }

        binding.btnEnter.setOnClickListener {
            doLogin()
            clearTextFields()
        }

    }

    private fun doLogin() {
        val name = binding.etLoginUserName.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()

        if (!name.isNullOrEmpty() && !password.isNullOrEmpty()) {
            obtainUserAndPassword(name, password)
        } else {
            Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtainUserAndPassword(name: String, passwordParam: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userNameFromDataStore = DataStoreManager.getUser(requireContext()).first()
            val passwordFromDataStore = DataStoreManager.getPassword(requireContext()).first()

            val isNameValid = userNameFromDataStore == name
            val isPasswordValid = passwordParam == passwordFromDataStore

            withContext(Dispatchers.Main) {
                checkCredentials(isNameValid, isPasswordValid)
            }
        }
    }

    private fun checkCredentials(isNameValid: Boolean?, isPasswordValid: Boolean?) {
        if (isNameValid == true && isPasswordValid == true) {
            lifecycleScope.launch(Dispatchers.IO) {
                DataStoreManager.setUserLogged(requireContext())
            }
            goToMainActivity()
            hideKeyBoard()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                if (isNameValid != null && !isNameValid) {
                    Toast.makeText(requireContext(), "Nombre no válido", Toast.LENGTH_SHORT).show()
                } else if (isPasswordValid != null && !isPasswordValid) {
                    Toast.makeText(requireContext(), "Contraseña no válida", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // Comprobamos que el usuario este en nuestra bbdd
    private suspend fun checkIsUserLogged() {
        DataStoreManager.getIsUserLogged(requireContext()).collect { isUserLogged ->
            if (isUserLogged) {
                with(Dispatchers.Main) {
                    goToMainActivity()
                }
            }
        }
    }

    // Vamos a mainActivity
    private fun goToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    // Vamos al fragmento de registro
    private fun goToRegister() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)
    }

    // Para ocultar el teclado
    private fun hideKeyBoard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus

        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //Borramos los campos
    private fun clearTextFields() {
        binding.etLoginUserName.text.clear()
        binding.etLoginPassword.text.clear()
    }

}