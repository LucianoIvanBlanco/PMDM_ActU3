package com.utad.ideasapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.utad.ideasapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var _binding: ActivityMainBinding
    private val binding: ActivityMainBinding get () = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fcvBottomNavigation.id)
        val controller = navHostFragment?.findNavController()

        if (controller != null) {
            binding.bnvMain.setupWithNavController(controller)
        }


    }


}