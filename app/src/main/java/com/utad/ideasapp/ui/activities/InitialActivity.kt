package com.utad.ideasapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.utad.ideasapp.databinding.ActivityInitialBinding

class InitialActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityInitialBinding
    private val binding: ActivityInitialBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }



}