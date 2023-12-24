package com.utad.ideasapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.utad.ideasapp.data_store.DataStoreManager
import com.utad.ideasapp.databinding.FragmentRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {

    private lateinit var _binding: FragmentRegisterBinding
    private val binding: FragmentRegisterBinding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateAccount.setOnClickListener {
            createUser()
        }
    }

    // Comprobamos que los campos no esten vacios, las contraseñas coincidan y creamos el usuario
    private fun createUser() {
        val userName = binding.etRegisterUserName.text.toString()
        val password = binding.etRegisterPassword.text.toString()
        val password2 = binding.etRegisterPassword2.text.toString()

        if (!userName.isNullOrEmpty() && !password.isNullOrEmpty() && !password2.isNullOrEmpty()) {

            if (password == password2) {
                lifecycleScope.launch(Dispatchers.IO) {
                    DataStoreManager.saveUser(requireContext(), userName, password)
                }
                Toast.makeText(requireContext(), "Se ha creado el usuario", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
        }


    }
}
