package com.utad.ideasapp.room.model

import androidx.room.Embedded
import androidx.room.Relation

data class IdeaDetailRelation(
    @Embedded
    val idea: Idea,

    @Relation(
        parentColumn = "id",   // El ID de la Idea
        entityColumn = "ideaId" // El campo en Detail que referencia a la Idea
    )
    val detailList: List<Detail>
)