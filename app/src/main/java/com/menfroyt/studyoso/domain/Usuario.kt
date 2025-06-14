package com.menfroyt.studyoso.domain

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String?, // o Date si prefieres
    val correo: String,
    val contrasena: String // hash almacenado
)