package com.menfroyt.studyoso.data.entities


import androidx.room.*
@Entity(tableName = "Usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellido")
    val apellido: String,

    @ColumnInfo(name = "fecha_nacimiento")
    val fechaNacimiento: String, // Usar convertidor de tipo si guardas Date

    @ColumnInfo(name = "correo")
    val correo: String,

    @ColumnInfo(name = "contrasena")
    val contrasena: String // aquí almacena hash (bcrypt)
)