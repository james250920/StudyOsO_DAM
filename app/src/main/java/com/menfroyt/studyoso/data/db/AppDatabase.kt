package com.menfroyt.studyoso.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.menfroyt.studyoso.data.dao.CalificacionDao
import com.menfroyt.studyoso.data.dao.CursoDao
import com.menfroyt.studyoso.data.dao.HorarioDao
import com.menfroyt.studyoso.data.dao.TareaDao
import com.menfroyt.studyoso.data.dao.TipoPruebaDao
import com.menfroyt.studyoso.data.dao.UsuarioDao
import com.menfroyt.studyoso.data.entities.Calificacion
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.Horario
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.data.entities.Usuario

@Database(
    entities = [
        Usuario::class,
        Curso::class,
        TipoPrueba::class,
        Horario::class,
        Tarea::class,
        Calificacion::class
               ], version = 1, exportSchema = true)
internal abstract class AppDatabase: RoomDatabase(){
    abstract fun UsuarioDao(): UsuarioDao
    abstract fun CursoDao(): CursoDao
    abstract fun TipoPruebaDao(): TipoPruebaDao
    abstract fun HorarioDao(): HorarioDao
    abstract fun TareaDao(): TareaDao
    abstract fun CalificacionDao(): CalificacionDao


    companion object{
        @Volatile
        private var INSTANCE : AppDatabase?=null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "TESTOSO_DB"
                ).build().also { INSTANCE = it }
            }
        }
    }
}