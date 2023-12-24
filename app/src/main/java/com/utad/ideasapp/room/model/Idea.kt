package com.utad.ideasapp.room.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Idea(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var ideaName: String,
    var ideaDescription: String,
    var ideaPriority: Int,
    var ideaStatus: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: Bitmap?
)



