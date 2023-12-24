package com.utad.ideasapp.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.utad.ideasapp.R
import com.utad.ideasapp.databinding.ActivityCreateBinding
import com.utad.ideasapp.room.model.Idea
import com.utad.ideasapp.ui.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateActivity : AppCompatActivity() {

    //region -------- Launchers
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            /**
             * El resultado de la petición de permiso, nos devolverá true o false dependiendo
             * de lo que pulsara el usuario
             * */
            if (isGranted) {
                //Si el usuario concedió el permiso, podemos abrir la galería
                openGallery()
            } else {
                //Si el usuario denegó el permiso,debemos decirle porqué es importante que lo conceda
                showPermissionDialog()
            }
        }

    private var settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //Una vez vuelve el usuario de ajustes, volvemos a comprobar si ha concedido los permisos
            checkPermissions()
        }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedImage: Uri? = data.data
                    selectedPhoto = convertUriToBitmap(selectedImage)
                    imageBitmap = selectedPhoto // Actualizar imageBitmap con la foto seleccionada
                    binding.ivIdeaDetail.setImageBitmap(selectedPhoto)
                } else {
                    Toast.makeText(this, "OYE, NO HAY FOTO", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "OYE, NO HAY FOTO", Toast.LENGTH_SHORT).show()
            }
        }

    private var selectedPhoto: Bitmap? = null

    //endregion


    private lateinit var _binding: ActivityCreateBinding
    private val binding get() = _binding
    private var imageBitmap: Bitmap? = null

    private val database by lazy { (application as MyApplication).dataBase.ideaDao() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRadioButtons()

        binding.btnImageSelection.setOnClickListener {
            checkPermissions()
        }

        binding.btnSaveIdeaDetail.setOnClickListener {
            saveIdea()
        }


    }

    private fun setUpRadioButtons() {
        binding.rbIdeaLow.setOnClickListener {
            clearRadioButtonsSelection()
            binding.rbIdeaLow.isChecked = true
        }

        binding.rbIdeaMedium.setOnClickListener {
            clearRadioButtonsSelection()
            binding.rbIdeaMedium.isChecked = true
        }

        binding.rbIdeaHigh.setOnClickListener {
            clearRadioButtonsSelection()
            binding.rbIdeaHigh.isChecked = true
        }
    }

    private fun clearRadioButtonsSelection() {
        binding.rbIdeaLow.isChecked = false
        binding.rbIdeaMedium.isChecked = false
        binding.rbIdeaHigh.isChecked = false
    }

    private fun checkPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                showPermissionDialog()
            }

            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Required")
            .setMessage("This app requires permission to access your photos.")
            .setPositiveButton("Open Settings") { _, _ ->
                goToSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun goToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        settingsLauncher.launch(intent)
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imageLauncher.launch(intent)
    }

    private fun convertUriToBitmap(uri: Uri?): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri!!)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveIdea() {
        Log.d("CreateActivity", "Save Idea Initiated")
        val ideaName = binding.etIdeaName.text.toString()
        val ideaDescription = binding.etIdeaDescription.text.toString()
        val ideaPriority = when {
            binding.rbIdeaLow.isChecked -> 1
            binding.rbIdeaMedium.isChecked -> 2
            binding.rbIdeaHigh.isChecked -> 3
            else -> 0
        }

        if (ideaName.isNotEmpty() && ideaDescription.isNotEmpty() && ideaPriority != 0) {

            val defaultImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_launcher_background
            ) // Reemplaza 'default image' con tu predeterminada

            val newIdea = Idea(
                ideaName = ideaName,
                ideaDescription = ideaDescription,
                ideaPriority = ideaPriority,
                ideaStatus = 0,
                image = imageBitmap ?: defaultImage
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("CreateActivity", "Saving Idea: $newIdea")
                    database.saveIdea(newIdea)
                    withContext(Dispatchers.Main) {
                        Log.d("CreateActivity", "Idea Saved Successfully")
                        Toast.makeText(this@CreateActivity, "Idea Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
            Log.d("CreateActivity", "Validation Failed")
        }
    }


}


