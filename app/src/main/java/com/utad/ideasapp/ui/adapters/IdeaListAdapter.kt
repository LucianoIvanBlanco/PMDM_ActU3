package com.utad.ideasapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.utad.ideasapp.databinding.ItemIdeaBinding
import com.utad.ideasapp.room.model.Idea

class IdeaListAdapter(
    val goToDetail: (ideaId: Int) -> Unit,
    val deleteIdea: (idea: Idea) -> Unit
) : ListAdapter<Idea, IdeaListAdapter.IdeaViewHolder>(IdeaCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIdeaBinding.inflate(inflater, parent, false)
        return IdeaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        val idea = getItem(position)

        Log.d("IdeaListAdapter", "Binding idea with ID: ${idea.id}")

        holder.binding.tvItemName.text = idea.ideaName
        holder.binding.tvDescription.text = idea.ideaDescription
        holder.binding.tvPrority.text = when (idea.ideaPriority) {
            1 -> "low"
            2 -> "medium"
            3 -> "high"
            else -> "undefined"
        }

        // Asignar estado
        holder.binding.tvStatus.text = when (idea.ideaStatus) {
            1 -> "Pendiente"
            2 -> "En Proceso"
            3 -> "Terminado"
            else -> "undefined"
        }

        holder.binding.cvIdea.setOnClickListener {
            Log.d("IdeaListAdapter", "Idea clicked with ID: ${idea.id}")
            goToDetail(idea.id)
        }

        holder.binding.btnDeleteIdea.setOnClickListener { deleteIdea(idea) }

    }

    inner class IdeaViewHolder(val binding: ItemIdeaBinding) :
        RecyclerView.ViewHolder(binding.root)


    object IdeaCallback : DiffUtil.ItemCallback<Idea>() {
        override fun areItemsTheSame(oldItem: Idea, newItem: Idea): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Idea, newItem: Idea): Boolean {
            return oldItem.ideaName == newItem.ideaName &&
                    oldItem.ideaDescription == newItem.ideaDescription &&
                    oldItem.ideaPriority == newItem.ideaPriority &&
                    oldItem.ideaStatus == newItem.ideaStatus
        }
    }
}
