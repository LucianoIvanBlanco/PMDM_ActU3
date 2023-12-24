package com.utad.ideasapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.utad.ideasapp.data_store.DataStoreManager
import com.utad.ideasapp.databinding.FragmentUserDataBinding
import com.utad.ideasapp.ui.activities.InitialActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDataFragment : Fragment() {

    private lateinit var _binding: FragmentUserDataBinding
    private val binding: FragmentUserDataBinding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("UserDataFragment", "onViewCreated called")

        loadUserData()

        binding.btnDelete.setOnClickListener {
            Log.d("UserDataFragment", "Delete button clicked")
            deleteUser()

        }

        binding.btnExit.setOnClickListener {
            Log.d("UserDataFragment", "Exit button clicked")
            lifecycleScope.launch(Dispatchers.IO) {
                DataStoreManager.logoutUser(requireContext())
                withContext(Dispatchers.Main) {
                    returnToLogin()
                }
            }
        }
    }

    private fun loadUserData() {
        Log.d("UserDataFragment", "loadUserData called")
        lifecycleScope.launch {
            val userNameFlow = DataStoreManager.getUser(requireContext())
            val passwordFlow = DataStoreManager.getPassword(requireContext())

            userNameFlow.combine(passwordFlow) { userName, password ->
                Pair(userName, password)
            }.collect { (userName, password) ->
                withContext(Dispatchers.Main) {
                    binding.tvUserName.text = userName
                    binding.tvUserPass.text = password
                }
            }
        }
    }


    private fun returnToLogin() {
        Log.d("UserDataFragment", "Returning to login")
        val intent = Intent(requireContext(), InitialActivity::class.java)
        requireActivity().finish()
        startActivity(intent)

    }

    private fun deleteUser() {
        Log.d("UserDataFragment", "Deleting user")
        lifecycleScope.launch(Dispatchers.IO) {
            DataStoreManager.clearUser(requireContext())
            withContext(Dispatchers.Main) {
                returnToLogin()
            }
        }
    }
}