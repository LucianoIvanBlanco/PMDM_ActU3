package com.utad.ideasapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.utad.ideasapp.databinding.FragmentIdeasListBinding
import com.utad.ideasapp.room.model.Idea
import com.utad.ideasapp.ui.MyApplication
import com.utad.ideasapp.ui.activities.CreateActivity
import com.utad.ideasapp.ui.adapters.IdeaListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdeasListFragment : Fragment() {

    private lateinit var _binding: FragmentIdeasListBinding
    private val binding get() = _binding
    private lateinit var adapter: IdeaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIdeasListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("IdeasListFragment", "onViewCreated")

        adapter = IdeaListAdapter(

            goToDetail = { ideaId ->
                Log.d("IdeasListFragment", "Navigating to detail with ID: $ideaId")
                val action =
                    IdeasListFragmentDirections.actionIdeasListFragmentToIdeaDetailFragment2(ideaId)
                findNavController().navigate(action)
            },
            deleteIdea = { idea ->
                deleteIdea(idea)
            }
        )

        binding.rvIdeasList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIdeasList.adapter = adapter

        binding.fabAddIdea.setOnClickListener {
            val intent = Intent(requireContext(), CreateActivity::class.java)
            startActivity(intent)
        }

        observeIdeasList()
    }

    private fun deleteIdea(idea: Idea) {
        val application = requireActivity().application as MyApplication
        lifecycleScope.launch(Dispatchers.IO) {
            application.dataBase.ideaDao().deleteIdea(idea)
        }
    }

    private fun observeIdeasList() {
        val application: MyApplication = requireActivity().application as MyApplication
        lifecycleScope.launch {
            application.dataBase.ideaDao().getAllIdeas().collect { ideas ->
                withContext(Dispatchers.Main) {
                    adapter.submitList(ideas)
                }
            }
        }
    }
}
