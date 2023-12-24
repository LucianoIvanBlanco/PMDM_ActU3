package com.utad.ideasapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.utad.ideasapp.room.model.Detail
import com.utad.ideasapp.room.model.Idea
import com.utad.ideasapp.room.model.IdeaDetailRelation
import kotlinx.coroutines.flow.Flow

@Dao
 interface IdeaDao {
    //region ---- IDEA ----
    //Para guardar datos nuevos en la lista
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveIdea(idea: Idea)

    @Insert
    suspend fun saveManyIdeas(ideas: List<Idea>)

    @Update
    suspend fun updateIdea(idea: Idea)

   @Query("SELECT * FROM idea")
   fun getAllIdeas(): Flow<List<Idea>>

    @Query("SELECT * FROM idea WHERE id=:ideaId")
    fun getIdeaDetail(ideaId: Int): Flow<Idea>

    @Delete
    suspend fun deleteIdea(idea: Idea)
    //endregion ---- IDEA ----

    //region ---- DETAIL ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDetail(detail: Detail)

    /**
     * Si usamos un flow para devolver los datos y tenerlos actualizados,
     * deberemos quitar el prefijo suspend de la función
     */
    @Query("SELECT * FROM detail WHERE ideaId = :ideaId")
    fun getDetails(ideaId: Int): Flow<List<Detail>>

    @Delete
    fun deleteDetail(detail: Detail)

    @Update
    suspend fun updateDetail(detail: Detail)

    /**
     * Si queremos hacer una consulta de dos tablas relacionadas, deberemos marcar esa Query como
     *  @Transaction
     *  e indicar que la función devolverá el objeto de esa relación.
     */
    @Transaction
    @Query("SELECT * FROM Idea WHERE id = :ideaId")
    fun getIdeaDetailsRelation(ideaId: Int): Flow<IdeaDetailRelation>
}