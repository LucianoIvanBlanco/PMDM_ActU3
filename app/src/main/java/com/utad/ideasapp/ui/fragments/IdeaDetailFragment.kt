package com.utad.ideasapp.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.utad.ideasapp.R
import com.utad.ideasapp.databinding.FragmentIdeaDetailBinding
import com.utad.ideasapp.room.model.Detail
import com.utad.ideasapp.ui.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class IdeaDetailFragment : Fragment() {

    private lateinit var _binding: FragmentIdeaDetailBinding
    private val binding get() = _binding
    private val args: IdeaDetailFragmentArgs by navArgs()

    private val ideaDetails = mutableListOf<String>() // Lista para almacenar detalles

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openGallery() else showToast("Permiso denegado")
        }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedPhoto = convertUriToBitmap(uri)
                    binding.ivIdeaDetail.setImageBitmap(selectedPhoto)
                } ?: showToast("No seleccionaste ninguna foto")
            } else {
                showToast("Error al seleccionar la imagen")
            }
        }
    private var selectedPhoto: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIdeaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioButtons()
        setupViewListeners()
        loadIdeaDetails(args.ideaId)
    }

    private fun setupRadioButtons() {
        val priorityListener = View.OnClickListener { view ->
            handleRadioButtonSelection(view.id, isPriority = true)
        }
        binding.rbIdeaLow.setOnClickListener(priorityListener)
        binding.rbIdeaMedium.setOnClickListener(priorityListener)
        binding.rbIdeaHigh.setOnClickListener(priorityListener)

        val statusListener = View.OnClickListener { view ->
            handleRadioButtonSelection(view.id, isPriority = false)
        }
        binding.rbIdeaStatus1.setOnClickListener(statusListener)
        binding.rbIdeaStatus2.setOnClickListener(statusListener)
        binding.rbIdeaStatus3.setOnClickListener(statusListener)
    }

    private fun handleRadioButtonSelection(selectedButtonId: Int, isPriority: Boolean) {
        if (isPriority) {
            binding.rbIdeaLow.isChecked = selectedButtonId == R.id.rb_idea_low
            binding.rbIdeaMedium.isChecked = selectedButtonId == R.id.rb_idea_medium
            binding.rbIdeaHigh.isChecked = selectedButtonId == R.id.rb_idea_high
        } else {
            binding.rbIdeaStatus1.isChecked = selectedButtonId == R.id.rb_idea_status1
            binding.rbIdeaStatus2.isChecked = selectedButtonId == R.id.rb_idea_status2
            binding.rbIdeaStatus3.isChecked = selectedButtonId == R.id.rb_idea_status3
        }
    }

    private fun setupViewListeners() {
        binding.ivIdeaDetail.setOnClickListener { checkPermissionsAndOpenGallery() }
        binding.btnSaveIdeaDetail.setOnClickListener { saveIdeaDetailsAndNavigateBack() }
    }

    private fun checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            openGallery()
        }
    }

    private fun saveIdeaDetailsAndNavigateBack() {
        val newDetail = binding.etNewDetail.text.toString()
        if (newDetail.isNotEmpty()) {
            saveNewDetailToRoom(newDetail)
            binding.etNewDetail.text.clear()
            updateIdeaInRoom(args.ideaId)
            findNavController().popBackStack()
        } else {
            showToast("Por favor, ingresa un detalle")
        }
    }

    private fun loadIdeaDetails(ideaId: Int) {
        val application = requireActivity().application as MyApplication
        lifecycleScope.launch(Dispatchers.IO) {
            val ideaWithDetails =
                application.dataBase.ideaDao().getIdeaDetailsRelation(ideaId).firstOrNull()
            ideaWithDetails?.let { ideaDetailRelation ->
                withContext(Dispatchers.Main) {
                    val idea = ideaDetailRelation.idea

                    // Actualizar todos los campos de la interfaz de usuario con los datos de la idea
                    binding.tvIdeaTitle.text = idea.ideaName
                    binding.tvIdeaDescription.text = idea.ideaDescription
                    binding.ivIdeaDetail.setImageBitmap(idea.image)

                    // Configurar los RadioButtons de Priority
                    binding.rbIdeaLow.isChecked = idea.ideaPriority == 1
                    binding.rbIdeaMedium.isChecked = idea.ideaPriority == 2
                    binding.rbIdeaHigh.isChecked = idea.ideaPriority == 3

                    // Configurar los RadioButtons de Status
                    binding.rbIdeaStatus1.isChecked = idea.ideaStatus == 1
                    binding.rbIdeaStatus2.isChecked = idea.ideaStatus == 2
                    binding.rbIdeaStatus3.isChecked = idea.ideaStatus == 3

                    // Cargar los detalles de la idea
                    loadIdeaDetailsList(ideaId)
                }
            }
        }
    }

    private fun loadIdeaDetailsList(ideaId: Int) {
        val application = requireActivity().application as MyApplication
        lifecycleScope.launch(Dispatchers.IO) {
            val details = application.dataBase.ideaDao().getDetails(ideaId).firstOrNull()
            details?.let { detailList ->
                withContext(Dispatchers.Main) {
                    // Limpiar la lista de detalles existente
                    ideaDetails.clear()

                    // Agregar los detalles de la relación a la lista
                    ideaDetails.addAll(detailList.map { it.detail })

                    // Actualizar el TextView con los detalles
                    binding.tvShowDetail.text =
                        ideaDetails.joinToString("\n\n") // Mostrar detalles con saltos de línea
                }
            }
        }
    }

    private fun saveNewDetailToRoom(newDetail: String) {
        // Obtener la instancia de la base de datos de la aplicación
        val application = requireActivity().application as MyApplication

        // Crear un nuevo objeto Detail con el ideaId de la idea actual
        val detail = Detail(ideaId = args.ideaId, detail = newDetail)

        // Guardar el nuevo detalle en la base de datos
        lifecycleScope.launch(Dispatchers.IO) {
            application.dataBase.ideaDao().saveDetail(detail)

            // Actualizar la lista de detalles en el UI
            loadIdeaDetailsList(args.ideaId)
        }
    }

    private fun updateIdeaInRoom(ideaId: Int) {
        // Obtener la instancia de la base de datos de la aplicación
        val application = requireActivity().application as MyApplication

        val ideaName = binding.tvIdeaTitle.text.toString()
        val ideaDescription = binding.tvIdeaDescription.text.toString()
        val ideaPriority = getSelectedPriority()
        val ideaStatus = getSelectedStatus()
        val id = ideaId

        if (ideaName.isNotEmpty() && ideaDescription.isNotEmpty() && ideaPriority != 0) {
            val defaultImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_launcher_background
            ) // Imagen predeterminada

            lifecycleScope.launch(Dispatchers.IO) {
                val idea = application.dataBase.ideaDao().getIdeaDetail(ideaId).firstOrNull()
                idea?.let {
                    it.id = id
                    it.ideaName = ideaName
                    it.ideaDescription = ideaDescription
                    it.ideaPriority = ideaPriority
                    it.ideaStatus = ideaStatus
                    it.image = selectedPhoto
                        ?: defaultImage // Usa la imagen seleccionada o la predeterminada

                    try {
                        Log.d("IdeaDetailFragment", "Updating Idea: $it")
                        application.dataBase.ideaDao().updateIdea(it)
                        withContext(Dispatchers.Main) {
                            Log.d("IdeaDetailFragment", "Idea Updated Successfully")
                            Toast.makeText(requireContext(), "Idea Updated", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().popBackStack()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_LONG).show()
            Log.d("IdeaDetailFragment", "Validation Failed")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imageLauncher.launch(intent)
    }

    private fun convertUriToBitmap(uri: Uri?): Bitmap? {
        uri ?: return null
        return try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getSelectedPriority(): Int {
        return when {
            binding.rbIdeaLow.isChecked -> 1
            binding.rbIdeaMedium.isChecked -> 2
            binding.rbIdeaHigh.isChecked -> 3
            else -> 0
        }
    }

    private fun getSelectedStatus(): Int {
        return when {
            binding.rbIdeaStatus1.isChecked -> 1
            binding.rbIdeaStatus2.isChecked -> 2
            binding.rbIdeaStatus3.isChecked -> 3
            else -> 0
        }
    }
}


