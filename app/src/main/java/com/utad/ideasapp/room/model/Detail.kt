package com.utad.ideasapp.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Detail(
    @PrimaryKey(autoGenerate = true)
    val detailId: Int = 0, // Clave primaria única para cada detalle
    val ideaId: Int,   // Clave foránea para referenciar a Idea
    val detail: String
)